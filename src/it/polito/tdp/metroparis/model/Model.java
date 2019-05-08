package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	//posso inserire la classe EdgeTraversedGraphListener come privata nel modello, 
	//in questo modo ha accesso a tutte le variabili di istanza , senza doverle passa re come parametro
	
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate ;
	private Map<Integer, Fermata> fermateIdMap;
	Map <Fermata, Fermata> backVisit;
	
	
	public void creaGrafo() {
		// Crea l'oggetto grafo
		this.grafo = new SimpleDirectedGraph<>(DefaultEdge.class);
		
		// Aggiungi i vertici
		MetroDAO dao = new MetroDAO();
		this.fermate = dao.getAllFermate();
		
		
		//crea idMap
		this.fermateIdMap = new HashMap<>();
		for(Fermata f : this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		Graphs.addAllVertices(grafo, fermate);
		
		//PRIMO APPROCCIO : doppio ciclo for e dati due vertici, controllo se c'è una connessione  con una query
		// Semplice ma in alcuni casi è lento,si richiama ogni volta il database con una nuova query
		
		// Aggiungi gli archi
		// Tramite due cicli for enumero tutte le possibili combinazioni di stazione partenza - stazione arrivo
		/*
		for(Fermata partenza : this.grafo.vertexSet()) { //per ogni fermata del grafo (che considero come partenza)
			for(Fermata arrivo : this.grafo.vertexSet()) { //navigo sulle altre fermate del grado e vedo se queste 
														   //possono essere stazioni di arrivo
				
				//se esiste una connessione tra le due stazioni, aggiungo l'arco al grafo
				if(dao.esisteConnessione (partenza,arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
			
			}
		}
		*/
		
		//SECONDO APPROCCIO: per ogni stazione di partenza, fare una query che restituisce una lista di fermate di arrivo
		//Faccio un livello di iterazioni in meno in Java
		
		// Aggiungi gli archi
		for(Fermata partenza : this.grafo.vertexSet()) {
			List<Fermata> arrivi = dao.stazioniArrivo(partenza, fermateIdMap);
			
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo); //Aggiungo tutti gli archi uscenti dalla partenza
			}
		}
		
		//TERZO APPROCCIO: faccio fare tutto al database/dao con query più complesse
		//Aggiungi gli archi
		
	}
	
	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		List<Fermata> risultato = new ArrayList<Fermata>();
		backVisit = new HashMap<>();  
		
		// Classe che crea un nuovo iteratore e lo associa a questo grafo
		// Da un punto di partenza specificato nel secondo parametro 
		// (se non specificato è scelto casualmente)
		
		GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo, source);
		//GraphIterator<Fermata,DefaultEdge> it = new DepthFirstIterator<>(this.grafo, source);
		
								// E' possibile definire questa classe come interna al Model
								// In questo modo può accedere alle variabili di istanza senza doverle passare come parametro
		it.addTraversalListener(new EdgeTraversedGraphListener(grafo, backVisit));
		
		/* CLASSE INLINE, a cui non si da' un nome
		it.addTraversalListener(new TraversalListener<Fermata,DefaultEdge>(){
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
			
			}
			@Override
			public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {
				// TODO Auto-generated method stub
				
			}

		});
		*/
		
		backVisit.put(source, null); //nodo radice dell'albero di visita è l'unico nodo che non ha un padre
		
		while(it.hasNext()) {
			risultato.add(it.next());
		}
		return risultato;
	}

	//Faccio la visita a partire da una sorgente fino alla fermata passata come parametro
	public List<Fermata> percorsoFinoA(Fermata target){
		
		if(!backVisit.containsKey(target)) {
			// il target non è raggiungibile dalla source
			return null;
		}
		
		List<Fermata> percorso = new LinkedList<Fermata> ();
		
		Fermata f = target;
		
		while( f!= null) { // itero queste operazioni finchè f != null, f == null nel caso del nodo radice
		percorso.add(0,f);   //aggiungo la destizazione
		f = backVisit.get(f); 	// dalla mappa ottengo il valore associato alla chiave f, che corrisponde al padre di f (valore della mappa)
								// padre sarà il nuovo vertice che compone il percorso
		}
		return percorso;
	}
	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}

}
