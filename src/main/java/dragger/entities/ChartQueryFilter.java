package dragger.entities;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import dragger.entities.QueryColumn;
import dragger.entities.charts.Chart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_charts_filters_values")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChartQueryFilter {
	@Id
	@SequenceGenerator(name = "charts_filters_seq", sequenceName = "charts_filters_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "charts_filters_seq")
	private long id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "chartId")
	private Chart chart;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "column_id")
	private QueryColumn column;
	private long filterId;
	private String value;
}
