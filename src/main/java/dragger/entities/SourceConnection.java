package dragger.entities;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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

	@ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinTable(name = "edges", joinColumns = @JoinColumn(name = "connection_id", referencedColumnName = "id"),
									 inverseJoinColumns = @JoinColumn(name = "query_column_id", referencedColumnName = "columnId"))
	private Collection<QueryColumn> edges;
}
