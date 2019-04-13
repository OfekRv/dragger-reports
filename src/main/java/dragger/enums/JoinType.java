package dragger.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JoinType {
	InnerJoin("INNER JOIN"), LeftJoin("LEFT JOIN");
	private final String raw;
}
