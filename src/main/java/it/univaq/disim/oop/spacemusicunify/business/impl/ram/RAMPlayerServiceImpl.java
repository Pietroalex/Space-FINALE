package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.ArrayList;
import java.util.List;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.PlayerService;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;

public class RAMPlayerServiceImpl implements PlayerService {

	private PlayerState playerState;
	
	@Override
	public PlayerState getPlayerState() {
		return playerState;
	}
	@Override
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
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
