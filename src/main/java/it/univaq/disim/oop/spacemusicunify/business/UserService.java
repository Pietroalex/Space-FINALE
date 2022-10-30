package it.univaq.disim.oop.spacemusicunify.business;

import it.univaq.disim.oop.spacemusicunify.domain.*;

import java.util.Set;

public interface UserService {

	void add(User user) throws BusinessException;
	
	void modify(Integer id, String username, String password) throws BusinessException;

	void delete(User user) throws BusinessException;

	void add(Playlist playlist) throws BusinessException;

	void modify(Set<Song> songs, Playlist playlist) throws BusinessException;

	void delete(Playlist playlist) throws BusinessException;

	Set<Playlist> getAllPlaylists(User user) throws BusinessException;

	GeneralUser authenticate(String username, String password) throws BusinessException;
	
	Set<User> getAllUsers() throws BusinessException;
	
}
