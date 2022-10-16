package it.univaq.disim.oop.spacemusicunify.business.impl.ram;

import java.util.HashSet;
import java.util.Set;

import it.univaq.disim.oop.spacemusicunify.business.AlreadyExistingException;
import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.ObjectNotFoundException;
import it.univaq.disim.oop.spacemusicunify.business.PlayerService;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.business.impl.file.Utility;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

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
		SpacemusicunifyPlayer player = new SpacemusicunifyPlayer(user);
		storedPlayers.add(player);
		
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
			if(player.getUser().getId().intValue() == user.getId().intValue()) {
				return player;
			}
		}
		throw new ObjectNotFoundException("not_existing_player");
	}

	@Override
	public void updateDuration(SpacemusicunifyPlayer player, Duration duration) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException();
		player.setDuration(duration);
	}

	@Override
	public void updateVolume(SpacemusicunifyPlayer player, Double volume) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException();
		player.setVolume(volume);
	}

	@Override
	public void addSongToQueue(SpacemusicunifyPlayer player, Song newSong) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException();
		player.getQueue().add(newSong);
	}
	@Override
	public void deleteSongFromQueue(SpacemusicunifyPlayer player, Song song) throws BusinessException {

		if(song.getId().intValue() == player.getQueue().get(player.getCurrentSong()).getId().intValue()) {	//canzone in corso uguale a quella selezionata

			if(player.getQueue().size() > 1 ) {				//più canzoni in riproduzione

				if(player.getCurrentSong() + 1 == player.getQueue().size()) {		//ultima canzone in coda uguale a canzone in corso
					if(player.getMediaPlayer() != null) {
						player.getMediaPlayer().stop();
						player.getMediaPlayer().dispose();
						player.setMediaPlayer(null);
					}
					updateCurrentSong(player, player.getCurrentSong() - 1);

				} else {	//canzone corrente tra prima posizione e penultima
							/*if(spacemusicunifyPlayer.getCurrentSong() != 0) {

							} else {												//canzone corrente in prima posizione */
					if(player.getMediaPlayer() != null) {
						player.getMediaPlayer().stop();
						player.getMediaPlayer().dispose();
						player.setMediaPlayer(null);
					}
					//}
				}

			} else {									//una sola canzone in riproduzione
				if(player.getMediaPlayer() != null) {
					player.getMediaPlayer().stop();
					player.getMediaPlayer().dispose();
					player.setMediaPlayer(null);
				}
			}

		} else {																				//canzone in corso diversa da quella selezionata

			for(int i = 0; i < player.getQueue().size(); i++) {
				if(player.getQueue().get(i).equals(song)) {
					if (player.getCurrentSong() > i ) {

						updateCurrentSong(player, player.getCurrentSong() - 1);

						break;
					}
				}
			}
		}
		player.getQueue().remove(song);
	}
	@Override
	public void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException {
		if(position >= player.getQueue().size() || position < 0) throw new BusinessException("no_position");
		player.setCurrentSong(position);
	}
	@Override
	public void replaceCurrentSong(SpacemusicunifyPlayer player, Song song) throws BusinessException {
		if(player.getQueue().size() == 0) throw new BusinessException("no_songs");
		player.getQueue().set(player.getCurrentSong(), song);
	}
	
}
