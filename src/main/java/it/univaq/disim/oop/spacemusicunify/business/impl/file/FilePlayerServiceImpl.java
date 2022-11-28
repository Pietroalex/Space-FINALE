package it.univaq.disim.oop.spacemusicunify.business.impl.file;

import it.univaq.disim.oop.spacemusicunify.business.*;
import it.univaq.disim.oop.spacemusicunify.domain.Song;
import it.univaq.disim.oop.spacemusicunify.domain.User;
import it.univaq.disim.oop.spacemusicunify.view.SpacemusicunifyPlayer;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

public class FilePlayerServiceImpl implements PlayerService {

	private String playersFile;

	public FilePlayerServiceImpl(String playersFileName){
		this.playersFile = playersFileName;
	}


	@Override
	public void add(User user) throws BusinessException {
		try {
			FileData fileData = Utility.readAllRows(playersFile);
			for(String[] columns: fileData.getRows()) {
				if (columns[0].equals( user.getId().toString() ) ) {
					throw new AlreadyExistingException("Already Existing player for this user");
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
				row.append("[]");
				row.append(Utility.COLUMN_SEPARATOR);
				row.append(0);


				writer.println(row.toString());

			}
		} catch (IOException e) {
			throw new BusinessException(e);
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
			if(!check)throw new BusinessException("Object not found, This Player doesn't exist");
		} catch (IOException e) {
			throw new BusinessException(e);
		}

	}
	@Override
	public SpacemusicunifyPlayer getPlayer(User user) throws BusinessException {
		SpacemusicunifyPlayer player = null;
		try {
			FileData fileData = Utility.readAllRows(playersFile);

			for (String[] rows : fileData.getRows()) {
				if(rows[0].equals(user.getId().toString())) {
					player = (SpacemusicunifyPlayer) UtilityObjectRetriever.findObjectById(rows[0], playersFile);
				}
			}
		} catch (IOException e) {
			throw new BusinessException(e);
		}
		if (player == null) throw new BusinessException("Object not found, There is no player for this user");
		return player;
	}

	@Override
	public void updateDuration(SpacemusicunifyPlayer player, Duration duration) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException("Error in song queue for this player, empty queue");
		player.setDuration(duration);

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {

					String[] row = new String[]{rows[0], rows[1], String.valueOf(duration.toMillis()), rows[3], rows[4], rows[5] };
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
		if(player.getQueue() == null) throw new BusinessException("Error in volume updating for this player");
		player.setVolume(volume);

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {

					String[] row = new String[]{rows[0], String.valueOf(volume), rows[2], rows[3], rows[4], rows[5] };
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
	public void updateMute(SpacemusicunifyPlayer player, boolean mute) throws BusinessException {
		if(player.getQueue() == null) throw new BusinessException("Error in mute updating for this player");
		player.setMute(mute);

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {

					String[] row = new String[]{rows[0], rows[1], rows[2], String.valueOf(mute), rows[4], rows[5] };
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

		if(player.getQueue() == null) throw new BusinessException("Error in song queue for this player, empty queue");
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



					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3],  String.valueOf(queueIDS), rows[5] };
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
		if(!(player.getQueue().removeIf((Song songcheck) -> songcheck.getId().intValue() == song.getId().intValue()))) throw new BusinessException("Error in removal of the song, not removed");

		List<String> queueIDS = new ArrayList<>();
		for(Song songs : player.getQueue()){
			queueIDS.add(songs.getId().toString());
		}
		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {



					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], String.valueOf(queueIDS), rows[5] };
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
		if(position >= player.getQueue().size() || position < 0) throw new BusinessException("Error in position inside player");

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {
					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], rows[4], String.valueOf(position) };
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
		if(player.getQueue().size() == 0) throw new BusinessException("The song queue has no songs, cannot replace");

		try {

			FileData fileData = Utility.readAllRows(playersFile);
			int cont = 0;
			for(String[] rows: fileData.getRows()) {
				if(rows[0].equals(player.getUser().getId().toString())) {
					List<String> queueIDS = Utility.readArray(rows[4]);
					queueIDS.set(player.getCurrentSong(), song.getId().toString());

					String[] row = new String[]{rows[0], rows[1], rows[2], rows[3], String.valueOf(queueIDS), rows[5] };
					fileData.getRows().set(cont, row);

					player.getQueue().set(player.getCurrentSong(), song);
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
}
