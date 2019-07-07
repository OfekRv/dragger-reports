package dragger.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import dragger.entities.charts.Chart;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_dashboards")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Dashboard {
	@Id
	@SequenceGenerator(name = "dashboard_seq", sequenceName = "dashboard_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dashboard_seq")
	private long id;
	@Column(nullable = false)
	@ElementCollection
	private Collection<Chart> charts;
}