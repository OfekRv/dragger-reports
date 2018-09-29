package dragger.entities;

import java.util.Collection;
import java.util.Iterator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_connections")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SourceConnection {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	@ManyToMany
	@JoinColumn(name = "columnId")
	private Collection<QueryColumn> edges;

	// TODO: delete it and wirk on rationalquerygenerator
	public QueryColumn getFirstEdge() {
		return edges.stream().findFirst().get();
	}

	// TODO: delete it and wirk on rationalquerygenerator
	public QueryColumn getSecondEdge() {
		Iterator<QueryColumn> i = edges.iterator();
		i.next();
		return i.next();

	}
}
