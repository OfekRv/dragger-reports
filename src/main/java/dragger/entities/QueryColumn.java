package dragger.entities;

import java.sql.JDBCType;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_query_columns")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueryColumn {
	@Id
	@SequenceGenerator(name = "column_seq", sequenceName = "column_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "column_seq")
	private long columnId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String raw;

	@Column(nullable = true)
	private JDBCType dataType;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH, optional = false)
	@JoinColumn(name = "sourceId")
	private QuerySource source;

	@ManyToMany(mappedBy = "edges", fetch = FetchType.EAGER)
	private Collection<SourceConnection> connections;
}
