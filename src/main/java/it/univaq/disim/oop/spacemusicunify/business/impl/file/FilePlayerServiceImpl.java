package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.Album;
import it.univaq.disim.oop.spacemusicunify.domain.Audio;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilePlayerServiceImpl implements PlayerService {

	private static final String REPOSITORY_BASE = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data";
	private static final String playersFile = REPOSITORY_BASE + File.separator + "players.txt";

	private static Set<SpacemusicunifyPlayer> storedPlayers = new HashSet<>();
	private PlayerState playerState;

	@Override
	public PlayerState getPlayerState() {
		return playerState;
	}
	@Override
	public void add(User user) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playersFile);
			for(String[] columns: fileData.getRows()) {
				if (columns[0].equals( user.getId().toString() ) ) {
					throw new AlreadyExistingException("existing_player");
				}
			}
			try (PrintWriter writer = new PrintWriter(new File(playersFile))) {
				long counter = fileData.getCounter();
				writer.println((counter + 1));
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}

				StringBuilder row = new StringBuilder();
				row.append(user.getId());
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(Double.valueOf(0.5));
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(Double.valueOf(40.5));
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(false);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(false);
				row.append(Utility.COLUMN_SEPARATOR);
				row.append("[]");
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(0);


				writer.println(row.toString());

			}
		} catch (IOException e) {
			throw new BusinessException();
		}

	}
	@Override
	public void delete(User user) throws BusinessException {
		boolean check = false;
		try {
			FileData fileData = Utility.readAllRows(playersFile);
			for(String[] rowsCheck: fileData.getRows()) {
				if(rowsCheck[0].equals(user.getId().toString())) {

					check = true;
					//aggiorno il file players.txt
					try (PrintWriter writer = new PrintWriter(new File(playersFile))) {
						writer.println(fileData.getCounter());
						for (String[] rows : fileData.getRows()) {
							if (rows[0].equals(user.getId().toString())) {
								//jump line
								continue;
							} else {
								writer.println(String.join("§", rows));
							}
						}
					}

					break;
				}
			}
			if(!check)throw new BusinessException("not_existing_player");
		} catch (IOException e) {
			throw new BusinessException();
		}

	}
	@Override
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;

	}
	@Override
	public SpacemusicunifyPlayer getPlayer(User user) throws BusinessException {
		SpacemusicunifyPlayer player = null;
		for(SpacemusicunifyPlayer players : storedPlayers) {
			if(players.getUser().getId().intValue() == user.getId().intValue()) {
				return players;
			}
		}
		try {
			FileData fileData = Utility.readAllRows(playersFile);

			for (String[] rows : fileData.getRows()) {
				if(rows[0].equals(user.getId().toString())) {
					player = (SpacemusicunifyPlayer) UtilityObjectRetriever.findObjectById(rows[0], playersFile);
					storedPlayers.add(player);
				}
			}
		} catch (IOException e) {
			throw new BusinessException();
		}
		if (player == null) throw  new ObjectNotFoundException("not_found_player");
		return player;
	}

	@Override
	public void updateDuration(SpacemusicunifyPlayer player, Duration duration) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException();
		player.setDuration(duration);

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {

					String[] row = new String[]{rows[0], rows[1], String.valueOf(duration.toMillis()), rows[3], rows[4], rows[5], rows[6] };
					fileData.getRows().set(cont, row);

					break;
				}
				cont++;
			}

			try(PrintWriter writer = new PrintWriter(new File(playersFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void updateVolume(SpacemusicunifyPlayer player, Double volume) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException();
		player.setVolume(volume);

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {



					String[] row = new String[]{rows[0], String.valueOf(volume), rows[2], rows[3], rows[4], rows[5], rows[6] };
					fileData.getRows().set(cont, row);

					break;
				}
				cont++;
			}

			try(PrintWriter writer = new PrintWriter(new File(playersFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
	}

	@Override
	public void addSongToQueue(SpacemusicunifyPlayer player, Song newSong) throws BusinessException {

		if(player.getQueue() == null) throw new BusinessException();
		player.getQueue().add(newSong);

		List<String> queueIDS = new ArrayList<>();
		for(Song song : player.getQueue()){
			queueIDS.add(song.getId().toString());
		}
		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {



					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], rows[4], String.valueOf(queueIDS), rows[6] };
					fileData.getRows().set(cont, row);

					break;
				}
				cont++;
			}

			try(PrintWriter writer = new PrintWriter(new File(playersFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
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

		List<String> queueIDS = new ArrayList<>();
		for(Song songs : player.getQueue()){
			queueIDS.add(songs.getId().toString());
		}
		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {



					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], rows[4], String.valueOf(queueIDS), rows[6] };
					fileData.getRows().set(cont, row);

					break;
				}
				cont++;
			}

			try(PrintWriter writer = new PrintWriter(new File(playersFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}


	}
	@Override
	public void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException {
		if(position >= player.getQueue().size() || position < 0) throw new BusinessException("impossibile scorrere la coda");

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {
					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], rows[4], rows[5], String.valueOf(position) };
					fileData.getRows().set(cont, row);

					player.setCurrentSong(position);
					break;
				}
				cont++;
			}

			try(PrintWriter writer = new PrintWriter(new File(playersFile))){
				writer.println(fileData.getCounter());
				for (String[] rows : fileData.getRows()) {
					writer.println(String.join(Utility.COLUMN_SEPARATOR, rows));
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}


	}
	@Override
	public void replaceCurrentSong(SpacemusicunifyPlayer player, Song song) throws BusinessException {
		player.getQueue().set(player.getCurrentSong(), song);
		//throw new BusinessException();
	}

	/*@Override
        public MediaPlayer getMediaPlayer() {
            return mediaPlayer;
        }
        @Override
        public void startMediaPlayer(Media media) {
            this.mediaPlayer = new MediaPlayer(media);
        }
        @Override
        public Duration getLastDuration() {
            return lastDuration;
        }
        @Override
        public void setLastDuration(Duration lastDuration) {
            this.lastDuration = lastDuration;

        }
        @Override
        public Boolean getPlayerOnPlay() {
            return playerOnPlay;
        }
        @Override
        public void setPlayerOnPlay(Boolean playerOnPlay) {
            this.playerOnPlay = playerOnPlay;
        }
        @Override
        public Double getPlayerVolume() {
            return volume;
        }
        @Override
        public void setPlayerVolume(Double volume) {
            this.volume = volume;
        }
        @Override
        public Boolean isMute() {
            return mute;
        }
        @Override
        public void setMute(Boolean mute) {
            this.mute = mute;
        }
        @Override
        public Song getLastSong() {
            return lastSong;
        }
        @Override
        public void setLastSong(Song song) {
            this.lastSong = song;
        }
        @Override
        public PlayerState getPlayerState() {
            return playerState;
        }
    */





	/*@Override
	public void addSongToQueue(User utente, Song canzone) {

		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { List<String> songQueue =
		 * Utility.leggiArray(righe[5]); songQueue.add(String.valueOf(canzone.getId()));
		 * String[] row = new String[] {righe[0], righe[1], righe[2], righe[3],
		 * righe[4], songQueue.toString()}; fileData.getRighe().set(cont, row);
		 * utente.getSongQueue().add(canzone); break; } cont++; } try (PrintWriter
		 * writer = new PrintWriter(new File(usersFile))) {
		 * writer.println(fileData.getContatore()); for (String[] righe :
		 * fileData.getRighe()) { writer.println(String.join(Utility.SEPARATORE_COLONNA,
		 * righe)); } } } catch (IOException e) { e.printStackTrace(); }

	}

	@Override
	public void deleteSongFromQueue(User utente, Song canzone) {

		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { List<String> songQueue =
		 * Utility.leggiArray(righe[5]);
		 * songQueue.remove(String.valueOf(canzone.getId())); String[] row = new
		 * String[] {righe[0], righe[1], righe[2], righe[3], righe[4],
		 * songQueue.toString()}; fileData.getRighe().set(cont, row); List<Song> lista =
		 * new ArrayList<>(utente.getSongQueue()); for(Song song :
		 * utente.getSongQueue()) { if(song.getId().equals(canzone.getId()))
		 * lista.remove(song); } utente.setSongQueue(lista); break; } cont++; } try
		 * (PrintWriter writer = new PrintWriter(new File(usersFile))) {
		 * writer.println(fileData.getContatore()); for (String[] righe :
		 * fileData.getRighe()) { writer.println(String.join(Utility.SEPARATORE_COLONNA,
		 * righe)); } } } catch (IOException e) { e.printStackTrace(); }

	}

	@Override
	public void updateCurrentSong(User utente, int position) { //aggiorna la canzone corrente scorrendo la coda

		 * try { FileData fileData = Utility.readAllRows(usersFile); int cont = 0;
		 * for(String[] righe : fileData.getRighe()) {
		 * if(righe[0].equals(utente.getId().toString())) { String[] row = new String[]
		 * {righe[0], righe[1], righe[2], righe[3], String.valueOf(position), righe[5]};
		 * fileData.getRighe().set(cont, row); utente.setcurrentPosition(position);
		 * break; } cont++; } try (PrintWriter writer = new PrintWriter(new
		 * File(usersFile))) { writer.println(fileData.getContatore()); for (String[]
		 * righe : fileData.getRighe()) {
		 * writer.println(String.join(Utility.SEPARATORE_COLONNA, righe)); } } } catch
		 * (IOException e) { e.printStackTrace(); }

	}

	@Override
	public void replaceCurrentSong(User utente, Song canzone) { //aggiorna la canzone corrente con una canzone a scelta
		try {
			FileData fileData = Utility.readAllRows(usersFile);
			int cont = 0;
			for(String[] righe : fileData.getRighe()) {
				if(righe[0].equals(utente.getId().toString())) {
					List<String> songQueue = Utility.leggiArray(righe[5]);
					songQueue.set(Integer.parseInt(righe[4]), String.valueOf(canzone.getId()));
					String[] row = new String[] {righe[0], righe[1], righe[2], righe[3], righe[4], songQueue.toString()};
					fileData.getRighe().set(cont, row);
					utente.getSongQueue().set(utente.getcurrentPosition(), canzone);
					break;
				}
				cont++;
			}
			try (PrintWriter writer = new PrintWriter(new File(usersFile))) {
				writer.println(fileData.getContatore());
				for (String[] righe : fileData.getRighe()) {
					writer.println(String.join(Utility.SEPARATORE_COLONNA, righe));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
}
