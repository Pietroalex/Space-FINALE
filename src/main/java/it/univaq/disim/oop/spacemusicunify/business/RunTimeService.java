package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;

public class RunTimeService {
	
	private static RunTimeService instance = new RunTimeService();
	private static String search;
	private static User user;
	private static SpacemusicunifyPlayer player;
	
	private RunTimeService() {}
	
	public static RunTimeService getInstance() {
		return instance;
	}

	public static String getSearch() {
		return search;
	}

	public static void setSearch(String search) {
		RunTimeService.search = search;
	}

	public static User getCurrentUser() {
		return user;
	}

	public static void setCurrentUser(User user) {
		RunTimeService.user = user;
	}

	public static SpacemusicunifyPlayer getPlayer() {
		return player;
	}

	public static void setPlayer(SpacemusicunifyPlayer player) {
		RunTimeService.player = player;
	}
}
