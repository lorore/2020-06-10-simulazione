package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {
	
	private Graph<Actor, DefaultWeightedEdge> graph;
	private ImdbDAO dao;
	private Map<Integer, Actor> idMap;
	private Map<Actor, Actor> predecessore;
	
	
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
	
	public List<Actor> attoriRaggiungibili(Actor partenza) {
		BreadthFirstIterator<Actor, DefaultWeightedEdge> bfv = 
				new BreadthFirstIterator<>(this.graph, partenza) ;
		
		this.predecessore = new HashMap<>() ;
		this.predecessore.put(partenza, null) ;
		
		bfv.addTraversalListener(new TraversalListener<Actor, DefaultWeightedEdge>() {
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
			}
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
			}
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				DefaultWeightedEdge arco = e.getEdge() ;
				Actor a = graph.getEdgeSource(arco);
				Actor b = graph.getEdgeTarget(arco);
				// ho scoperto 'a' arrivando da 'b' (se 'b' lo conoscevo) b->a
				if(predecessore.containsKey(b) && !predecessore.containsKey(a)) {
					predecessore.put(a, b) ;
//					System.out.println(a + " scoperto da "+ b) ;
				} else if(predecessore.containsKey(a) && !predecessore.containsKey(b)) {
					// di sicuro conoscevo 'a' e quindi ho scoperto 'b'
					predecessore.put(b, a) ;
//					System.out.println(b + " scoperto da "+ a) ;
				}
			}
			@Override
			public void vertexTraversed(VertexTraversalEvent<Actor> e) {
//				System.out.println(e.getVertex());
//				Fermata nuova = e.getVertex() ;
//				Fermata precedente = vertice adiacente a 'nuova' che sia già raggiunto
//						(cioè è già presente nelle key della mappa);
//				predecessore.put(nuova, precedente) ;
			}
			@Override
			public void vertexFinished(VertexTraversalEvent<Actor> e) {
			}
		});
		
//		DepthFirstIterator<Fermata, DefaultEdge> dfv = 
//				new DepthFirstIterator<>(this.grafo, partenza) ;
		
		List<Actor> result = new ArrayList<>() ;
		
		while(bfv.hasNext()) {
			Actor f = bfv.next() ;
			result.add(f) ;
		}
		
		return result ;
	}
	
	
	
	
}
