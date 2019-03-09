package dragger.entities;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

	@Nullable
	@ManyToMany
	@JoinColumn(name = "columnId")
	private Collection<QueryColumn> countColumns;

	@Nullable
	@ManyToMany
	@JoinColumn(name = "columnId")
	private Collection<QueryColumn> groupBys;

	@JsonIgnore
	public Collection<QuerySource> getSources() {
		Collection<QuerySource> sources = new ArrayList<>();
		columns.forEach(column -> {
			if (!sources.contains(column.getSource())) {
				sources.add(column.getSource());
			}
		});

		groupBys.forEach(column -> {
			if (!sources.contains(column.getSource())) {
				sources.add(column.getSource());
			}
		});

		countColumns.forEach(column -> {
			if (!sources.contains(column.getSource())) {
				sources.add(column.getSource());
			}
		});

		return sources;
	}
}