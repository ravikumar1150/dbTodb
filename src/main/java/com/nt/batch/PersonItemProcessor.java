package com.nt.batch;

import org.springframework.batch.item.ItemProcessor;

import com.nt.person.Person;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {

    @Override
    public Person process(Person person) throws Exception {
        String firstName = person.firstName().toUpperCase();
        String lastName = person.lastName().toUpperCase();
        return new Person(firstName, lastName);
    }
}
