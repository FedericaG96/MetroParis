package it.polito.tdp.metroparis.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model {
	
	private Graph<Fermata, DefaultEdge> grafo;
	private List<Fermata> fermate ;
	private Map<Integer, Fermata> fermateIdMap;
	
	
	
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
		Map <Fermata, Fermata> back = new HashMap<>();  
		
		// Classe che crea un nuovo iteratore e lo associa a questo grafo
		// Da un punto di partenza specificato nel secondo parametro 
		// (se non specificato è scelto casualmente)
		//GraphIterator<Fermata, DefaultEdge> it = new BreadthFirstIterator<>(this.grafo, source);
		GraphIterator<Fermata,DefaultEdge> iterator = new DepthFirstIterator<>(this.grafo, source);
		
		while(iterator.hasNext()) {
			risultato.add(iterator.next());
		}
		
		
		return risultato;
	}

	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	public List<Fermata> getFermate() {
		return fermate;
	}

}
