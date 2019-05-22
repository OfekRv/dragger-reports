package dragger.entities.charts;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "drg_charts_execution_results")
@TypeDef(name="jsonb", typeClass = JsonBinaryType.class)

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChartExecutionResult {
	@Id
	private ChartExecutionResultId id;

	@Column(columnDefinition = "json")
	@Type(type = "jsonb")

	// @Convert(converter = ChartResultsToJsonConventer.class)
	private ChartResult executionResult;
	// private Collection<ChartColumnResult> executionResult;
}
