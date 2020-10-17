package com.yea.com.survive.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yea.com.survive.model.Alive;
import com.yea.com.survive.util.FileOperation;

@Component
public class SurviveService {

	private static final Logger logger = LogManager.getLogger(SurviveService.class);

	@Autowired
	private FileOperation fileOperation;

	private final String path = "files" + File.separatorChar;
	private String inputFileName;
	private String heroName;

	private List<String> inputFile;
	private List<Alive> enemies;
	private Alive hero;
	private int resourceDistance;

	public SurviveService(@Value("${inputFileName}") String inputFileName, @Value("${heroName}") String heroName) {
		this.inputFileName = path + inputFileName;
		this.heroName = heroName;
	}

	@PostConstruct
	private void prepareInputData() {
		getInputFile();
		getResourceDistanceInfo();
		getHeroInfo();
		getUniqueEnemies();
		getEnemyInfos();
		sortEnemiesByDistance();
		logEnemies();
	}

	public List<Alive> getEnemyList() {
		return enemies;
	}

	public Alive getHero() {
		return hero;
	}
	
	public int getResourceDistance() {
		return resourceDistance;
	}

	private void logEnemies() {
		logger.info("Enemies:");
		for (Alive alive : enemies) {
			logger.info(alive.getName() + " - " + alive.getAttack() + " - " + alive.getHealth() + " - "
					+ alive.getPosition());
		}
	}

	private void sortEnemiesByDistance() {
		{
			int n = enemies.size();
			for (int i = 0; i < n - 1; i++)
				for (int j = 0; j < n - i - 1; j++)
					if (enemies.get(j).getPosition() > enemies.get(j + 1).getPosition()) {

						Alive temp = enemies.get(j);
						enemies.set(j, enemies.get(j + 1));
						enemies.set(j + 1, temp);
					}
		}

	}

	private void getEnemyInfos() {

		List<Alive> dupliceEnemies = new ArrayList<Alive>();

		for (int i = 0; i < enemies.size(); i++) {
			String enemyName = enemies.get(i).getName();
			for (String line : inputFile) {
				if (checkWordExist(line, enemyName) && checkWordExist(line, "hp")) {
					int health = getNumberValueFromLine(line);
					enemies.get(i).setHealth(health);
				}

				if (checkWordExist(line, enemyName) && checkWordExist(line, "attack")) {
					int attack = getNumberValueFromLine(line);
					enemies.get(i).setAttack(attack);
				}

				if (checkWordExist(line, enemyName) && checkWordExist(line, "position")) {
					int position = getNumberValueFromLine(line);
					if (enemies.get(i).getPosition() == 0) // bu enemy'den ilk defa yazıldı ..
					{
						enemies.get(i).setPosition(position);
					} else { // eğer aynı enemy'den birden fazla bulunyorsa o enemy'i common özellikleri ve
								// kendi position'u ile enemy listeme ekledim..
						Alive alive = new Alive();
						alive.setName(enemies.get(i).getName());
						alive.setAttack(enemies.get(i).getAttack());
						alive.setPosition(enemies.get(i).getPosition());
						alive.setHealth(enemies.get(i).getHealth());
						alive.setPosition(position);
						dupliceEnemies.add(alive);
					}

				}
			}
		}

		for (Alive alive : dupliceEnemies) {
			enemies.add(alive);
		}
	}

	private boolean checkWordExist(String line, String word) {
		Pattern pattern = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE);
		Matcher match = pattern.matcher(line);

		return match.find();
	}

	private void getUniqueEnemies() {
		enemies = new ArrayList<Alive>();

		for (String line : inputFile) {
			if (checkWordExist(line, "is Enemy")) {
				enemies.add(createNewAlive(line.substring(0, line.indexOf("is") - 1)));
			}
		}
	}

	private Alive createNewAlive(String enemyName) {
		Alive alive = new Alive();
		alive.setName(enemyName);
		return alive;
	}

	private void getHeroInfo() {
		boolean gotHealth = false;
		boolean gotAttack = false;

		hero = new Alive();
		hero.setName(heroName);
		hero.setPosition(0);
		for (String line : inputFile) {
			if (checkWordExist(line, "Hero") && checkWordExist(line, "Hp")) {
				int health = getNumberValueFromLine(line);
				hero.setHealth(health);
				gotHealth = true;
			}

			if (checkWordExist(line, "Hero") && checkWordExist(line, "attack")) {
				int attack = getNumberValueFromLine(line);
				hero.setAttack(attack);
				gotAttack = true;
			}

			if (gotHealth && gotAttack)
				break;
		}
	}

	private void getResourceDistanceInfo() {

		boolean gotResourceDistance = false;

		for (String line : inputFile) {
			if (checkWordExist(line, "Resources")) {
				resourceDistance = getNumberValueFromLine(line);
				gotResourceDistance = true;
			}
			if (gotResourceDistance)
				break;
		}
	}

	private int getNumberValueFromLine(String line) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(line);

		matcher.find();
		return Integer.parseInt(matcher.group());
	}

	private void getInputFile() {
		inputFile = fileOperation.readFile(inputFileName);
		logger.info("Input file is:");
		for (String line : inputFile) {
			logger.info(line);
		}
	}

}
