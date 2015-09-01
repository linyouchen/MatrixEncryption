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
	
	
	public double[] getD() 
	{
		return D;
	}
	public int[] getF() 
	{
		return F;
	}

	public void setD(double[] newD)
	{
		D = newD;
	}
	
	public void setF(int[] newF)
	{
		F = newF;
	}

	
}