package dragger.entities;

import java.util.Collection;

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

	/*
	 * private QuerySource firstEdgeSource; private QuerySource
	 * secondEdgeSource;
	 */
	public QueryColumn getFirstEdge() {
		// TODO Auto-generated method stub
		return null;
	}

	public QueryColumn getSecondEdge() {
		// TODO Auto-generated method stub
		return null;
	}
}
