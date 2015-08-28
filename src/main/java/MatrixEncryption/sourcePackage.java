package MatrixEncryption;

import java.io.Serializable;

public class sourcePackage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double[] D = {0};
	private int[] F = {0};

	
	public sourcePackage(double[] newD, int[] newF) {
		D = newD;
		F = newF;
	}
	
	
	double[] getD() 
	{
		return D;
	}
	int[] getF() 
	{
		return F;
	}

	void setD(double[] newD)
	{
		D = newD;
	}
	
	void setF(int[] newF)
	{
		F = newF;
	}

	
}