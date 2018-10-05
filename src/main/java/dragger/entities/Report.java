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
@Table(name = "drg_reports")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Report {
	@Id
	@SequenceGenerator(name = "report_seq", sequenceName = "report_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq")
	private long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false)
	//@JsonIgnore
	private Query query;
}
