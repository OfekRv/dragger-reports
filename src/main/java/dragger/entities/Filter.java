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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_filters")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Filter {
	@Id
	@SequenceGenerator(name = "filter_seq", sequenceName = "filter_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "filter_seq")
	private long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	private String rawFilter;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "column_id", nullable = true)
	private Collection<ChartQueryFilter> chartFilters;
}
