package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	
	public List<String> getGeneri(){
		String sql="SELECT DISTINCT mg.genre "
				+ "FROM movies_genres mg "
				+ "ORDER BY mg.genre ";
		Connection conn = DBConnect.getConnection();
		List<String> result=new ArrayList<>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				result.add(res.getString("genre"));
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Director> listAllDirectors(){
		String sql = "SELECT * FROM directors";
		List<Director> result = new ArrayList<Director>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				
				result.add(director);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void getVertici(Map<Integer, Actor> idMap, String g) {
		String sql="SELECT * "
				+ "FROM actors a "
				+ "WHERE a.id IN "
				+ "(SELECT r.actor_id "
				+ "FROM roles r , movies m, movies_genres mg "
				+ "WHERE r.movie_id=m.id AND m.id=mg.movie_id AND r.movie_id=mg.movie_id AND genre=?) ";
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				if(!idMap.containsKey(actor.getId())) {
					idMap.put(actor.getId(), actor);
				}
				
			}
			conn.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<Adiacenza> getArchi(Map<Integer, Actor> idMap, String g){
		String sql="SELECT r.actor_id AS a1, r1.actor_id AS a2, COUNT(DISTINCT r.movie_id) AS peso "
				+ "FROM roles r, roles r1 "
				+ "WHERE r.actor_id<>r1.actor_id AND r.movie_id=r1.movie_id "
				+ "AND r.movie_id IN ( "
				+ "SELECT mg.movie_id "
				+ "FROM movies_genres mg "
				+ "WHERE mg.genre=?) "
				+ "GROUP BY r.actor_id, r1.actor_id ";
		
		Connection conn = DBConnect.getConnection();
		List<Adiacenza> result=new ArrayList<Adiacenza>();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, g);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				if(idMap.containsKey(res.getInt("a1")) && idMap.containsKey(res.getInt("a2"))){
					Actor a1=idMap.get(res.getInt("a1"));
					Actor a2=idMap.get(res.getInt("a2"));
					int peso=res.getInt("peso");
					result.add(new Adiacenza(a1, a2, peso));
				}
				
				
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
}
