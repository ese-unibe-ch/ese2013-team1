package ch.unibe.sport.utils;

import junit.framework.TestCase;

public class ObjecksonTest extends TestCase {

	public static class Foo {
		private String hash;
		private String[] names;
		private Bar bar;	

		public void setHash(String hash){
			this.hash = hash;
		}

		public void setBar(Bar bar){
			this.bar = bar;
		}

		public void setNames(String[] names){
			this.names = names;
		}
		
		public String getHash() {
			return hash;
		}

		public String[] getNames() {
			return names;
		}

		public Bar getBar() {
			return bar;
		}
	}

	public static class Bar {
		private String address;

		public void setAddress(String address){
			this.address = address;
		}

		public String getAddress() {
			return address;
		}
	}
	
	public static class TestCaster{
		private short idShort;
		private Short idCShort;
		private int idInt;
		private Integer idInteger;
		private float idFloat;
		private Float idCFloat;
		private double idDouble;
		private Double idCDouble;
		
		public Integer getIdInteger() {
			return idInteger;
		}

		public void setIdInteger(Integer idInteger) {
			this.idInteger = idInteger;
		}

		public float getIdFloat() {
			return idFloat;
		}

		public void setIdFloat(float idFloat) {
			this.idFloat = idFloat;
		}

		public Float getIdCFloat() {
			return idCFloat;
		}

		public void setIdCFloat(Float idCFloat) {
			this.idCFloat = idCFloat;
		}

		public double getIdDouble() {
			return idDouble;
		}

		public void setIdDouble(double idDouble) {
			this.idDouble = idDouble;
		}

		public Double getIdCDouble() {
			return idCDouble;
		}

		public void setIdCDouble(Double idCDouble) {
			this.idCDouble = idCDouble;
		}

		public int getIdInt() {
			return idInt;
		}

		public void setIdInt(int id) {
			this.idInt = id;
		}

		public Short getIdCShort() {
			return idCShort;
		}

		public void setIdCShort(Short idCShort) {
			this.idCShort = idCShort;
		}

		public short getIdShort() {
			return idShort;
		}

		public void setIdShort(short idShort) {
			this.idShort = idShort;
		}
		
	}

	public void testFooBar(){
		String json = "{\"hash\":\"becw6-nc3q-qc3r4\",\"bar\":{\"address\":\"Groovestreet, 1\"},\"names\":[\"Pedro\",\"Muller\",\"Brown\"]}";
		Foo foo = Objeckson.fromJson(json, Foo.class);
		assertEquals("becw6-nc3q-qc3r4",foo.getHash());
		assertEquals("Pedro",foo.getNames()[0]);
		assertEquals("Muller",foo.getNames()[1]);
		assertEquals("Brown",foo.getNames()[2]);
		assertEquals("Groovestreet, 1",foo.getBar().getAddress());
	}
	
	public void testIntegerTo(){
		String json = "{\"idShort\":1337,\"idCShort\":1337,\"idInt\":1337,\"idInteger\":1337,\"idFloat\":1337,\"idCFloat\":1337,\"idDouble\":1337,\"idCDouble\":1337}";
		TestCaster test = Objeckson.fromJson(json, TestCaster.class);
		assertEquals(1337,test.getIdShort());
		assertEquals(1337,test.getIdCShort().shortValue());
		assertEquals(1337,test.getIdInt());
		assertEquals(Integer.valueOf(1337),test.getIdInteger());
		assertEquals(1337f,test.getIdFloat());
		assertEquals(1337f,test.getIdCFloat());
		assertEquals(1337d,test.getIdDouble());
		assertEquals(1337d,test.getIdCDouble());
	}
	
	public void testDoubleTo(){
		String json = "{\"idShort\":133.7,\"idCShort\":133.7,\"idInt\":133.7,\"idInteger\":133.7,\"idFloat\":133.7,\"idCFloat\":133.7,\"idDouble\":133.7,\"idCDouble\":133.7}";
		TestCaster test = Objeckson.fromJson(json, TestCaster.class);
		assertEquals(133,test.getIdShort());
		assertEquals(133,test.getIdCShort().shortValue());
		assertEquals(133,test.getIdInt());
		assertEquals(Integer.valueOf(133),test.getIdInteger());
		assertEquals(133.7f,test.getIdFloat());
		assertEquals(133.7f,test.getIdCFloat());
		assertEquals(133.7d,test.getIdDouble());
		assertEquals(133.7d,test.getIdCDouble());
	}
	
	public void testFooBarSpeed(){
		String json = "{\"idShort\":1337,\"idCShort\":1337,\"idInt\":133.7,\"idInteger\":133.7,\"idFloat\":133.7,\"idCFloat\":133.7,\"idDouble\":133.7,\"idCDouble\":133.7}";
		int N = 10000;
		Timer timer = new Timer();
		for (int i = 0; i < N; i++){
			Objeckson.fromJson(json, TestCaster.class);
		}
		Print.log("N: "+N+" time: "+timer.timeElapsed()+"ms");
	}
	
	public static class TestIntegerArray{
		private Integer[] arrayInteger;
		private int[] arrayInt;
		public Integer[] getArrayInteger() {
			return arrayInteger;
		}
		public void setArrayInteger(Integer[] array) {
			this.arrayInteger = array;
		}
		public int[] getArrayInt() {
			return arrayInt;
		}
		public void setArrayInt(int[] arrayInt) {
			this.arrayInt = arrayInt;
		}
	}
	
	public void testIntegerArray(){
		String json = "{\"arrayInteger\":[1,2],\"arrayInt\":[1,2]}";
		TestIntegerArray obj = Objeckson.fromJson(json, TestIntegerArray.class);
		assertEquals((Integer)1, obj.getArrayInteger()[0]);
		assertEquals((Integer)2, obj.getArrayInteger()[1]);
		assertEquals(1, obj.getArrayInt()[0]);
		assertEquals(2, obj.getArrayInt()[1]);
	}
}
