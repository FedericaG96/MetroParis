package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgetreversedGraphListener implements TraversalListener<Fermata, DefaultEdge>{

	Graph <Fermata , DefaultEdge> grafo ;
	Map <Fermata, Fermata> back; //mappa che punta dal basso verso l'alto
	
	public EdgetreversedGraphListener(Graph <Fermata , DefaultEdge> grafo, Map<Fermata, Fermata> back) {
			super();
			this.grafo = grafo;
			this.back = back;
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
		 * se non è orientato
		 */
		back.put(ev.getEdge().destinationVertex(), ev.getEdge().sourceVertex());
		
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
