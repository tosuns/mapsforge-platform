package de.fub.agg2graph.agg.strategy;

import java.util.HashMap;
import java.util.Map;

public class AttractionValue {
	public static Map<String, Double> value = new HashMap<String, Double>();
	
	/**
	 * Mapping Value N = 5 ; s1 = 5 ; s2 = 5 ; M = 1 ; k = 0.005
	 */
	public AttractionValue() {		
		value.put("0.5", 0.2349893228573277929974838725218);
		value.put("1.0", 0.47039229198758733988549322544592);
		value.put("1.5", 0.7066275677048794777673421542817);
		value.put("2.0", 0.94412399160516523673352837390209);
		value.put("2.5", 1.1833260688344379486370654549711);
		value.put("3.0", 1.4246999452330863994427143585582);
		value.put("3.5", 1.6687400882419830822134013121217);
		value.put("4.0", 1.915976923200196950835720874733);
		value.put("4.5", 2.1669857372297503569857337463347);
		value.put("5.0", 2.4223972473870150018761467820899);
		value.put("5.5", 2.6829103471123537779574643949483);
		value.put("6.0", 2.9493077083604921279235476260002);
		value.put("6.5", 3.2224751454927448998344704816528);
		value.put("7.0", 3.5034259697394077942062627736754);
		value.put("7.5", 3.793332022549655382583064920888);
		value.put("8.0", 4.0935637363781356323833941553772);
		value.put("8.5", 4.4057425278545490385201365917093);
		value.put("9.0", 4.7318102215392786507703291210112);
		value.put("9.5", 5.074122232928061081880566442715);
		value.put("10.0", 5.4355741681941698365613664824599);
		value.put("10.5", 5.8197755847910917811206268199015);
		value.put("11.0", 6.2312898660735165278956806915115);
		value.put("11.5", 6.6759641091700781713298713597262);
		value.put("12.0", 7.1613714008774574051734367313857);
		value.put("12.5", 7.6973579686445662520648505045031);
		value.put("13.0", 8.2965601526900554932134924882148);
		value.put("13.5", 8.9743372239117896659919015092187);
		value.put("14.0", 9.7464601606233170746478213236741);
		value.put("14.5", 10.621131777824040189812703487636);
		value.put("15.0", 11.58368626454190280235732640251);
		value.put("15.5", 12.587364856657719416476557278437);
		value.put("16.0", 13.573254141869636007567426200366);
		value.put("16.5", 14.502565018873911503498950076303);
		value.put("17.0", 15.363918301681614950484950411256);
		value.put("17.5", 16.161657059433340104848605686343);
		value.put("18.0", 16.905241079702734862283664473659);
		value.put("18.5", 17.604347021116244180051444341807);
		value.put("19.0", 18.267327262595646397365660896058);
		value.put("19.5", 18.900990523692225299365975436007);
		value.put("20.0", 19.510787623013908238445829128819);
	}
	
	public Double getValue(double distance) {
		String str;
		String strComma = "";
		double comma = distance - Math.floor(distance);
		
		if(0.25 < comma && comma < 0.75) {
			strComma = ".5";
			str = "" + (int)Math.floor(distance);
		}
		else {
			strComma = ".0";
			if(comma >= 0.75)
				str = "" + (int)Math.ceil(distance);
			else
				str = "" + (int)Math.floor(distance);
		}
		str = str.concat(strComma);
		return value.containsKey(str) ? value.get(str) : null;
	}
	
	public Double getKey(double distance) {
		String str;
		String strComma = "";
		double comma = distance - Math.floor(distance);
		
		if(0.25 < comma && comma < 0.75) {
			strComma = ".5";
			str = "" + (int)Math.floor(distance);
		}
		else {
			strComma = ".0";
			if(comma >= 0.75)
				str = "" + (int)Math.ceil(distance);
			else
				str = "" + (int)Math.floor(distance);
		}
		str = str.concat(strComma);
		return Double.parseDouble(str);
	}
	
	public static void main(String[] args) {
		AttractionValue a = new AttractionValue();
		System.out.println(a.getValue(0.71));
		System.out.println(a.getValue(0.75));
		System.out.println(a.getValue(1));
		System.out.println(a.getValue(1.249));
		System.out.println(a.getValue(1.26));
		System.out.println(a.getValue(1.5));
		System.out.println(a.getValue(7.3));
		System.out.println(a.getValue(7.6));
	}
}
