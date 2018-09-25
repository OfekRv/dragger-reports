package dragger.entities;

import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Query {
	@ManyToMany
	@JoinColumn(name = "id")
	private Collection<QueryColumn> columns;
	@ManyToMany
	@JoinColumn(name = "id")
	private Collection<QuerySource> sources;
	@ManyToMany
	@JoinColumn(name = "id")
	private Collection<SourceConnection> connections;
}
