package test;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(name="student")
@XmlAccessorType(XmlAccessType.NONE)
class Student {
	
	@XmlElement(name="name")
	private String name;
	
	@XmlElement(name="age")
	private int age;

	public Student() {
	}

	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
	public void setAge(int age) { this.age = age; }
	public int getAge() { return age; }
	
	public static void main(String[] args) throws JAXBException {
		Student student = new Student();
		student.setAge(25);
		// student.setName("FooBar");
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(Student.class);
		Marshaller m = context.createMarshaller();
		// m.marshal(new JAXBElement(new QName(Student.class.getSimpleName()), Student.class, student), writer);
		m.marshal(student, writer);
		System.out.println(writer.toString());
		
		Unmarshaller um = context.createUnmarshaller();
		Student abc = (Student)um.unmarshal(new StringReader(writer.toString()));
		System.out.println(abc.getAge());
	}
}