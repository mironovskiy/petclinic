package org.springframework.samples.petclinic.vet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class VetFormatter implements Formatter<Vet> {

	private final VetRepository vetRepository;

	@Autowired
	public VetFormatter(VetRepository vetRepository) {
		this.vetRepository = vetRepository;
	}

	@Override
	public Vet parse(String s, Locale locale) throws ParseException {
		Vet vet = vetRepository.findFirstByLastName(s);
		return vet;
	}

	@Override
	public String print(Vet vet, Locale locale) {
		return vet.getFirstName() + " " + vet.getLastName();
	}
}
