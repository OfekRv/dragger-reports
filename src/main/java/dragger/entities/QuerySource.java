package dragger.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "drg_query_sources")

@AllArgsConstructor
@Getter
@Setter
public class QuerySource {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private long id;
	@Column(nullable = false, unique = true)
	private String name;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private Collection<QueryColumn> columns;
	@Column(nullable = false)
	private String fromClauseRaw;
	@Column(nullable = false)
	private String whereClauseRaw;
	@Column(nullable = false)
	private String groupByClauseRaw;
}
