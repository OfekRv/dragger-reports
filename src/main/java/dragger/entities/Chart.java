package dragger.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_charts")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chart{
	@Id
	@SequenceGenerator(name = "chart_seq", sequenceName = "chart_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chart_seq")
	private long id;

	@Column(nullable = false)
	private Query query;
}
