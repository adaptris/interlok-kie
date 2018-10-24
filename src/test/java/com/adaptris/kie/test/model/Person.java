package com.adaptris.kie.test.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("person")
public class Person {

  private static final int EARLIEST_BIRTH_YEAR = 1946;

  public enum Gender {
    MALE, FEMALE;
    public static Gender fromOrdinal(int n) {
      return values()[n];
    }

  };

  // American definitions from
  // http://www.pewresearch.org/fact-tank/2018/04/11/millennials-largest-generation-us-labor-force/ft_15-05-11_millennialsdefined/
  public enum Generation {
    Before_These_Things_Mattered, // WW2 and before, we don't care for our purposes.
    BabyBoomer, // 1946-1964 ("Jones" -> 1955-1964??)
    GenerationX, // 1965-1980
    Millenial, // 1981-1997
    GenerationZ, // 1998 onwards
  };

  private String name;
  private int yearOfBirth;
  private Gender gender;
  private List<String> hobbies;
  private String profession;
  private Generation generation;

  public Person() {
    setGender(Gender.fromOrdinal(ThreadLocalRandom.current().nextInt(2)));
    setYearOfBirth(ThreadLocalRandom.current().nextInt(70) + EARLIEST_BIRTH_YEAR);
    setHobbies(new ArrayList<>());
    setProfession("");
    setGeneration(Generation.Before_These_Things_Mattered);
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Person withGender(Gender g) {
    setGender(g);
    return this;
  }

  public List<String> getHobbies() {
    return hobbies;
  }

  public Person withHobbies(String... hobbies) {
    setHobbies(new ArrayList<>(Arrays.asList(hobbies)));
    return this;
  }

  public void setHobbies(List<String> s) {
    this.hobbies = s;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProfession() {
    return profession;
  }

  public void setProfession(String s) {
    this.profession = s;
  }

  public Person withProfession(String profession) {
    setProfession(profession);
    return this;
  }

  public int getYearOfBirth() {
    return yearOfBirth;
  }

  public void setYearOfBirth(int yearOfBirth) {
    this.yearOfBirth = yearOfBirth;
  }

  public Person withYearOfBirth(int year) {
    setYearOfBirth(year);
    return this;
  }

  public Generation getGeneration() {
    return generation;
  }

  public void setGeneration(Generation g) {
    this.generation = g;
  }
}

