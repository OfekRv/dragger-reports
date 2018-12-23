package dragger.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

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
	@JoinColumn(name = "columnId")
	private Collection<QueryColumn> columns;

	public Collection<QuerySource> getSources() {
		Collection<QuerySource> sources = new ArrayList<>();
		columns.forEach(column -> {
			if (!sources.contains(column.getSource())) {
				sources.add(column.getSource());
			}
		});
		return sources;
	}
}