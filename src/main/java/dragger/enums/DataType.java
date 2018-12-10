package dragger.enums;

import java.sql.JDBCType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataType {
	
	NUMBER(JDBCType.NUMERIC),
	TEXT(JDBCType.VARCHAR),
	DATE(JDBCType.DATE);
	
	 @JsonProperty("name")
		private String name;
	 
	 @JsonProperty("type")
	private JDBCType type;
	
	private DataType(JDBCType type)
	{
		this.type = type;
		this.name = name();
	}
}
