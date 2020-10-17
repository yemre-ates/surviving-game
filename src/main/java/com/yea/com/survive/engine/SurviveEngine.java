package com.yea.com.survive.engine;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.yea.com.survive.model.Alive;
import com.yea.com.survive.service.SurviveService;
import com.yea.com.survive.util.FileOperation;

@Configuration
public class SurviveEngine {

	private static final Logger logger = LogManager.getLogger(SurviveEngine.class);

	private final String path = "files" + File.separatorChar;
	private String outputFileName;

	private List<Alive> enemies;
	private Alive hero;
	private int resourceDistance;

	@Autowired
	private SurviveService surviveService;

	@Autowired
	private FileOperation fileOperation;

	public SurviveEngine(@Value("${outputFileName}") String outputFileName) {
		this.outputFileName = path + outputFileName;
	}
	

	private void getResourceDistance() {
		resourceDistance = surviveService.getResourceDistance();
	}
	private void getFighters() {
		enemies = surviveService.getEnemyList();
		hero = surviveService.getHero();
		logger.info(
				"---------------------------------------------------------   FIGHT STARTING   ----------------------------------------------------------------");
	}

	public void fight() {
		boolean isReachedResource = false;
		deleteOutputIfExist();
		getFighters();
		getResourceDistance();

		fileOperation.writeFile(outputFileName, hero.getName() + " started journey with " + hero.getHealth() + " HP!");
		logger.info(hero.getName() + " started journey with " + hero.getHealth() + " HP!");

		for (int enemyIndex = 0; enemyIndex < enemies.size(); enemyIndex++) {

			do {
				if(resourceDistance >= enemies.get(enemyIndex).getPosition()) {
					attack(hero, enemies.get(enemyIndex));
					attack(enemies.get(enemyIndex), hero);
				}
				
				else {
					fileOperation.writeFile(outputFileName, System.lineSeparator() + hero.getName() +" already reached resource.");
					logger.info(hero.getName() +" already reached resource.");
					isReachedResource = true;
					break;
				}

			} while (hero.isAlive() && enemies.get(enemyIndex).isAlive());

			if(isReachedResource)
				break;
			
			if (hero.isAlive()) {

				fileOperation.writeFile(outputFileName, System.lineSeparator() + hero.getName() + " defeated "
						+ enemies.get(enemyIndex).getName() + " with " + hero.getHealth() + " HP remaining");
				logger.info(hero.getName() + " defeated " + enemies.get(enemyIndex).getName() + " with "
						+ hero.getHealth() + " HP remaining");

			} else if (enemies.get(enemyIndex).isAlive()) {

				fileOperation.writeFile(outputFileName, System.lineSeparator() + enemies.get(enemyIndex).getName() + " defeated "
						+ hero.getName() + " with " + enemies.get(enemyIndex).getHealth() + " HP remaining");
				logger.info(enemies.get(enemyIndex).getName() + " defeated " + hero.getName() + " with "
						+ enemies.get(enemyIndex).getHealth() + " HP remaining");
				fileOperation.writeFile(outputFileName, System.lineSeparator() + hero.getName() + " is Dead!! Last seen at position "
						+ enemies.get(enemyIndex).getPosition() + "!!");
				logger.info(hero.getName() + " is Dead!! Last seen at position " + enemies.get(enemyIndex).getPosition()
						+ "!!");
				break;

			}
		}
		
		if(hero.isAlive()) {
			fileOperation.writeFile(outputFileName, System.lineSeparator() +  hero.getName() + " Survived!");
			logger.info(hero.getName() + " Survived!");
		}
	}

	private void deleteOutputIfExist() {
		
		fileOperation.deleteFileIfExist(outputFileName);
		
	}

	private void attack(Alive attacker, Alive defender) {
		
		int damage = attacker.getAttack();
		int health = defender.getHealth() - damage;

		defender.setHealth(health);
	}
}
