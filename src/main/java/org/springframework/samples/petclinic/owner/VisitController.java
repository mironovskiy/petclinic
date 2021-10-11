/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.samples.petclinic.vet.Vets;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private final VisitRepository visits;

	private final PetRepository pets;

	private final VetRepository vets;


	public VisitController(VisitRepository visits, PetRepository pets, VetRepository vets) {
		this.visits = visits;
		this.pets = pets;
		this.vets = vets;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, Map<String, Object> model) {
		Pet pet = this.pets.findById(petId);
		pet.setVisitsInternal(this.visits.findByPetId(petId));
		model.put("pet", pet);
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	@ModelAttribute("vets")
	public Collection<Vet> findVets() {
		return this.vets.findAll();
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is called
	@GetMapping("/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		else {
			visit.setStatus("active");
			this.visits.save(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping("/owners/*/pets/{petId}/visits/{visitId}/edit")
	public String initVisitUpdateForm(@PathVariable("petId") int petId, @PathVariable("visitId") int visitId, Map<String, Object> model){
		Visit visit = visits.findById(visitId);
		model.put("visit", visit);
		return "pets/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String processVisitUpdateForm(@PathVariable("visitId") int visitId, @Valid Visit visit, BindingResult result, Map<String, Object> model){
		if (result.hasErrors()) {
			model.put("visit", visit);
			return "pets/createOrUpdateVisitForm";
		}
		else {
			visit.setStatus("active");
			visit.setId(visitId);
			this.visits.save(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/cancel")
	public String cancelVisit(@PathVariable("petId") int petId, @PathVariable("visitId") int visitId, Map<String, Object> model){
		Visit visit = visits.findById(visitId);
		visit.setStatus("canceled");
		this.visits.save(visit);
		model.put("visit", visit);
		return "redirect:/owners/{ownerId}";
	}

}
