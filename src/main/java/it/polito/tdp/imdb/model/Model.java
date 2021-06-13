package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private Graph<Actor, DefaultWeightedEdge> graph;
	private ImdbDAO dao;
	private Map<Integer, Actor> idMap;
	
	
	public Model() {
		dao=new ImdbDAO();
	}
	
	public String creaGrafo(String g) {
		idMap=new HashMap<>();
		graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.dao.getVertici(idMap, g);
		Graphs.addAllVertices(graph, idMap.values());
		System.out.println(this.graph.vertexSet().size());
		List<Adiacenza> archi=this.dao.getArchi(idMap, g);
		for(Adiacenza a: archi) {
			Actor a1=a.getA1();
			Actor a2=a.getA2();
			DefaultWeightedEdge e=graph.getEdge(a1, a2);
			if(e==null) {
				Graphs.addEdge(graph, a1, a2, a.getPeso());
			}
		}
		System.out.println(graph.edgeSet().size());
		String result="Num vertici: "+graph.vertexSet().size()+" Num archi: "+graph.edgeSet().size();
		return result;
	}

	
	public List<Actor> getAttori(){
		List<Actor> attori=new ArrayList<>(idMap.values());
		Collections.sort(attori);
		return attori;
	}
	
	public List<String> getGeneri(){
		return this.dao.getGeneri();
	}
}
