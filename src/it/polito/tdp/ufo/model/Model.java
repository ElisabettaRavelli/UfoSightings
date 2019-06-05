package it.polito.tdp.ufo.model;

import java.time.Year;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.ufo.db.SightingsDAO;

public class Model {
	
	//PER LA RICORSIONE
	//1-struttura dati finale
	//è una lista di stati in cui c'è lo stato di partenza e un insieme di altri stati (non ripetuti)
	
	//2-strutttura dati parziale
	//una lista definita nel metodo ricorsivo
	
	//3-condizione di terminazione 
	//dopo un determinato nodo, non ci sono più successori che on ho considerato
	
	//4-generare una nuova soluzione a partire da una soluzione parziale
	//dato l'ultimo nodo inserito in parziale, considero tutti i successori di del nodo che non ho ancora considerato
	
	//5-filtro
	//alla fine ritonerò una sola soluzione -> quella per cui la size() è massima
	
	//6-livello di ricorsione 
	//lunghezza del percorso pariale 
	
	//7-il caso iniziale
	//parziale contiene il mio nodo di partenza
	
	private List<String> ottima;
	private SightingsDAO dao;
	private List<String> stati;
	private Graph<String, DefaultEdge> grafo;
	
	public Model() {
		this.dao = new SightingsDAO(); 
	}
	
	public List<AnnoCount> getAnni(){
		return this.dao.getAnni();
	}
	
	public void creaGrafo(Year anno) {
		this.grafo = new SimpleDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		this.stati = this.dao.getStati(anno);
		Graphs.addAllVertices(this.grafo, this.stati);
		
		for(String s1: this.grafo.vertexSet()) {
			for(String s2: this.grafo.vertexSet()) {
				if(!s1.equals(s2)) {
					if(this.dao.esisteArco(s1,s2, anno)) {
						this.grafo.addEdge(s1, s2);
					}
				}
			}
		}
		System.out.println("Grafo creato!\n");
		System.out.println("Vertici: " + this.grafo.vertexSet().size()+"  ");
		System.out.println("Archi: " + this.grafo.edgeSet().size());
		
		
	}
	
	public int getNvertici() {
		return this.grafo.vertexSet().size();
	}
	public int getNarchi() {
		return this.grafo.edgeSet().size();
	}
	public List<String> getStati(){
		return this.stati;
	}
	public List<String> getSuccessori(String stato){
		return Graphs.successorListOf(this.grafo, stato);
	}
	public List<String> getPredecessori(String stato){
		return Graphs.predecessorListOf(this.grafo, stato);
	}
	
	
	public List<String> getRaggiungibili(String source){
		List<String> raggiungibili = new LinkedList<>();
		GraphIterator<String, DefaultEdge> it = new DepthFirstIterator<>(this.grafo, source);
		
		it.next();
		while(it.hasNext()) {
			raggiungibili.add(it.next());
		}
		return raggiungibili;
	}
	
	public List<String> getPercorsoMassimo(String partenza){
		this.ottima = new LinkedList<String>(); //ricreo da zero la mia soluzione ottima perchè la voglio ricalcolare
		List<String> parziale = new LinkedList<>();
		parziale.add(partenza);
		
		cercaPercorso(parziale);
		
		return this.ottima;
	}

	private void cercaPercorso(List<String> parziale) {
		//vedere se la soluzione corrente è migliore della ottima corrente
		if(parziale.size()>ottima.size()) {
			this.ottima = new LinkedList<>(parziale);
		}
		//ottengo tutti i candidati
		List<String> candidati = this.getSuccessori(parziale.get(parziale.size()-1));
		for(String candidato : candidati) {
			if(!parziale.contains(candidato)) {
				//è un candidato che non ho ancora considerato
				parziale.add(candidato);
				this.cercaPercorso(parziale);
				parziale.remove(parziale.size()-1);
			}
		}
	}

}
