package dragger.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drg_query_columns")

@AllArgsConstructor
@Getter
@Setter
public class QueryColumn {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String raw;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id", insertable = false, updatable = false)
	private QuerySource source;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Collection<SourceConnection> connections;
}
