package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ObjectNotFoundException;
import it.univaq.disim.oop.spacemusicunify.business.PlayerService;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;

public class RAMPlayerServiceImpl implements PlayerService {
	
	private static Set<SpacemusicunifyPlayer> storedPlayers = new HashSet<>();
	private PlayerState playerState;
	
	@Override
	public PlayerState getPlayerState() {
		return playerState;
	}
	@Override
	public void add(User user) throws BusinessException {
		for(SpacemusicunifyPlayer player : storedPlayers) {
			if(player.getUser() == user) {
				throw new AlreadyExistingException();
			}
		}
		SpacemusicunifyPlayer spacemusicunifyPlayer = new SpacemusicunifyPlayer(user);
		storedPlayers.add(spacemusicunifyPlayer);
		
	}
	@Override
	public void delete(User user) throws BusinessException {
		SpacemusicunifyPlayer player = null;
		boolean check = false;
		for(SpacemusicunifyPlayer playerCheck : storedPlayers) {
			if(playerCheck.getUser() == user) {
				check = true;
				storedPlayers.remove(player);
				break;
			}
		}
		if(!check) throw new ObjectNotFoundException("player not found");

	}
	@Override
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}
	@Override
	public SpacemusicunifyPlayer getPlayer(User user) throws BusinessException {
		for(SpacemusicunifyPlayer player : storedPlayers) {
			if(player.getUser() == user) {
				return player;
			}
		}
		throw new ObjectNotFoundException("player non trovato");
	}
	@Override
	public void addSongToQueue(SpacemusicunifyPlayer player, Song newSong) throws BusinessException {
		boolean check = false;
		for(Song song : player.getQueue()) {
			if(song == newSong) {
				check = true;
			}
		}
		if(!check) player.getQueue().add(newSong);
		else throw new BusinessException("canzone già presente in coda");
	}
	@Override
	public void deleteSongFromQueue(SpacemusicunifyPlayer player, Song song) throws BusinessException {
		if(!player.getQueue().remove(song)) throw new BusinessException("canzone non presente in coda");
	}
	@Override
	public void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException {
		if(position >= player.getQueue().size() || position < 0) throw new BusinessException("impossibile scorrere la coda");
		player.setCurrentSong(position);
	}
	@Override
	public void replaceCurrentSong(SpacemusicunifyPlayer player, Song song) throws BusinessException {
		player.getQueue().set(player.getCurrentSong(), song); 
		//throw new BusinessException();
	}
	
}
