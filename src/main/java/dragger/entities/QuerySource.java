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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_query_sources")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuerySource {
	@Id
	@SequenceGenerator(name = "source_seq", sequenceName = "source_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "source_seq")
	private long sourceId;
	@Column(nullable = false, unique = true)
	private String name;
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "columnId")
	@JsonManagedReference("source")
	private Collection<QueryColumn> columns;
	@Column(nullable = false)
	private String fromClauseRaw;
}
