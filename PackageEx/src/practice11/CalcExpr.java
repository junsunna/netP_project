// 1911249 나준선

package practice11;

import java.io.Serializable;

public class CalcExpr implements Serializable{
	double operand1, operand2;
	char operator;
	
	public CalcExpr(double operand1, char operator, double operand2) {
		this.operand1 = operand1;
		this.operator = operator;
		this.operand2 = operand2;
	}
}