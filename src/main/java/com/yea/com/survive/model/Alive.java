package com.yea.com.survive.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Alive {

	private String name;
	private int health;
	private int attack;
	private int position;
	
	public boolean isAlive() {
		return health > 0;
	}
}

