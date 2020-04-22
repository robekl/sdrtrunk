package io.github.dsheirer.dsp.filter.smoothing;

import java.util.HashMap;
import java.util.Map;

public class TriangularSmoothingFilter extends SmoothingFilter
{
	private static Map<Integer,double[]> mMap = new HashMap<>();

	static
	{
		mMap.put( 3, new double[]{ 0.25d,0.5d,0.25d } );
		mMap.put( 5, new double[]{ 0.1111111111d,0.2222222222d,0.3333333333d,0.2222222222d,0.1111111111d } );
		mMap.put( 7, new double[]{ 0.0625d,0.125d,0.1875d,0.25d,0.1875d,0.125d,0.0625d } );
		mMap.put( 9, new double[]{ 0.04d,0.08d,0.12d,0.16d,0.2d,0.16d,0.12d,0.08d,0.04d } );
		mMap.put( 11, new double[]{ 0.0277777778d,0.0555555556d,0.0833333333d,0.1111111111d,0.1388888889d,0.1666666667d,0.1388888889d,0.1111111111d,0.0833333333d,0.0555555556d,0.0277777778d } );
		mMap.put( 13, new double[]{ 0.0204081633d,0.0408163265d,0.0612244898d,0.0816326531d,0.1020408163d,0.1224489796d,0.1428571429d,0.1224489796d,0.1020408163d,0.0816326531d,0.0612244898d,0.0408163265d,0.0204081633d } );
		mMap.put( 15, new double[]{ 0.015625d,0.03125d,0.046875d,0.0625d,0.078125d,0.09375d,0.109375d,0.125d,0.109375d,0.09375d,0.078125d,0.0625d,0.046875d,0.03125d,0.015625d } );
		mMap.put( 17, new double[]{ 0.012345679d,0.024691358d,0.037037037d,0.049382716d,0.0617283951d,0.0740740741d,0.0864197531d,0.0987654321d,0.1111111111d,0.0987654321d,0.0864197531d,0.0740740741d,0.0617283951d,0.049382716d,0.037037037d,0.024691358d,0.012345679d } );
		mMap.put( 19, new double[]{ 0.01d,0.02d,0.03d,0.04d,0.05d,0.06d,0.07d,0.08d,0.09d,0.1d,0.09d,0.08d,0.07d,0.06d,0.05d,0.04d,0.03d,0.02d,0.01d } );
		mMap.put( 21, new double[]{ 0.0082644628d,0.0165289256d,0.0247933884d,0.0330578512d,0.041322314d,0.0495867769d,0.0578512397d,0.0661157025d,0.0743801653d,0.0826446281d,0.0909090909d,0.0826446281d,0.0743801653d,0.0661157025d,0.0578512397d,0.0495867769d,0.041322314d,0.0330578512d,0.0247933884d,0.0165289256d,0.0082644628d } );
		mMap.put( 23, new double[]{ 0.0069444444d,0.0138888889d,0.0208333333d,0.0277777778d,0.0347222222d,0.0416666667d,0.0486111111d,0.0555555556d,0.0625d,0.0694444444d,0.0763888889d,0.0833333333d,0.0763888889d,0.0694444444d,0.0625d,0.0555555556d,0.0486111111d,0.0416666667d,0.0347222222d,0.0277777778d,0.0208333333d,0.0138888889d,0.0069444444d } );
		mMap.put( 25, new double[]{ 0.0059171598d,0.0118343195d,0.0177514793d,0.0236686391d,0.0295857988d,0.0355029586d,0.0414201183d,0.0473372781d,0.0532544379d,0.0591715976d,0.0650887574d,0.0710059172d,0.0769230769d,0.0710059172d,0.0650887574d,0.0591715976d,0.0532544379d,0.0473372781d,0.0414201183d,0.0355029586d,0.0295857988d,0.0236686391d,0.0177514793d,0.0118343195d,0.0059171598d } );
		mMap.put( 27, new double[]{ 0.0051020408d,0.0102040816d,0.0153061224d,0.0204081633d,0.0255102041d,0.0306122449d,0.0357142857d,0.0408163265d,0.0459183673d,0.0510204082d,0.056122449d,0.0612244898d,0.0663265306d,0.0714285714d,0.0663265306d,0.0612244898d,0.056122449d,0.0510204082d,0.0459183673d,0.0408163265d,0.0357142857d,0.0306122449d,0.0255102041d,0.0204081633d,0.0153061224d,0.0102040816d,0.0051020408d } );
		mMap.put( 29, new double[]{ 0.0044444444d,0.0088888889d,0.0133333333d,0.0177777778d,0.0222222222d,0.0266666667d,0.0311111111d,0.0355555556d,0.04d,0.0444444444d,0.0488888889d,0.0533333333d,0.0577777778d,0.0622222222d,0.0666666667d,0.0622222222d,0.0577777778d,0.0533333333d,0.0488888889d,0.0444444444d,0.04d,0.0355555556d,0.0311111111d,0.0266666667d,0.0222222222d,0.0177777778d,0.0133333333d,0.0088888889d,0.0044444444d } );
	}
	
	public TriangularSmoothingFilter()
	{
		super( mMap, 3 );
	}

	@Override
	public SmoothingType getSmoothingType()
	{
		return SmoothingType.TRIANGLE;
	}
}
