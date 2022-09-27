package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.BusinessException;
import it.univaq.disim.oop.spacemusicunify.business.PlayerService;
import it.univaq.disim.oop.spacemusicunify.business.PlayerState;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.List;

public class FilePlayerServiceImpl implements PlayerService {

	private MediaPlayer mediaPlayer;
	private Duration lastDuration;
	private boolean playerOnPlay;
	private Double volume;
	private boolean mute;
	private Song lastSong;
	private PlayerState playerState;

	private static FilePlayerServiceImpl instance;

	public static FilePlayerServiceImpl getInstance() {
		if(instance == null) { instance = new FilePlayerServiceImpl();}
		return instance;
	}

	@Override
	public PlayerState getPlayerState() {
		return null;
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
	@Override
	public SpacemusicunifyPlayer getPlayer(User user) throws BusinessException {
		return null;
	}

	@Override
	public List<SpacemusicunifyPlayer> getAllPlayers() {
		return null;
	}

	@Override
	public void setPlayerState(PlayerState playerState) {
		this.playerState = playerState;
	}

	@Override
	public void addSongToQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException {

	}

	@Override
	public void deleteSongFromQueue(SpacemusicunifyPlayer player, Song canzone) throws BusinessException {

	}

	@Override
	public void updateCurrentSong(SpacemusicunifyPlayer player, int position) throws BusinessException {

	}

	@Override
	public void replaceCurrentSong(SpacemusicunifyPlayer player, Song canzone) throws BusinessException {

	}

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
