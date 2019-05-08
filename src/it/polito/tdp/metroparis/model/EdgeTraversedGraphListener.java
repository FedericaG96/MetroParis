package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedGraphListener implements TraversalListener<Fermata, DefaultEdge>{

	Graph <Fermata , DefaultEdge> grafo ;
	Map <Fermata, Fermata> backVisit; //mappa che punta dal basso verso l'alto
	
	public EdgeTraversedGraphListener(Graph <Fermata , DefaultEdge> grafo, Map<Fermata, Fermata> backVisit) {
			super();
			this.grafo = grafo;
			this.backVisit = backVisit;
		}
	
	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) {
		/*
		 * back codifica relazioni del tipo child -> parent
		 * 
		 * per un nuovo vertice child scoperto 
		 * devo avere che:
		 * - child è ancora sconosciuto, non ancora visitato
		 * - parent è già stato visitato
		 */
		
		Fermata sourceVertex = grafo.getEdgeSource(ev.getEdge());
		Fermata targetVertex = grafo.getEdgeTarget(ev.getEdge());
		
		/*
		 * se il grafo è orientato, allora  source == parent, target = child
		 * se non è orientato, potrebbe essere al contrario ... 
		 */
		
		// Devo vedere che il figlio non sia ancora una chiave della mappa
		// e che il padre sia già presente nella mappa
		
		if (!backVisit.containsKey(targetVertex) && backVisit.containsKey(sourceVertex)) {
			
			//Caso di grafo orientato
			backVisit.put(targetVertex, sourceVertex); 
			//Aggiungo alla mappa l'arco che codifica il fatto che il target (figlio) si raggiunge dalla sorgente (padre)
		
		} else if(!backVisit.containsKey(sourceVertex) && backVisit.containsKey(targetVertex)) {
			backVisit.put(sourceVertex, targetVertex); 
		}
		
		
	}
	
	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
		// TODO Auto-generated method stub
		
	}

}
