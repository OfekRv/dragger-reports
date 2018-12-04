package dragger.contracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter	
public class ReportQueryFilterContract {
	private long filterId;
	private long columnId;
	private String value;
}
