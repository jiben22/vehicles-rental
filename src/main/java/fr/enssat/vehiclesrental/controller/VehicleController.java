package fr.enssat.vehiclesrental.controller;

import fr.enssat.vehiclesrental.constants.ControllerConstants.VehicleController.*;
import fr.enssat.vehiclesrental.model.Car;
import fr.enssat.vehiclesrental.model.Motorbike;
import fr.enssat.vehiclesrental.model.Plane;
import fr.enssat.vehiclesrental.model.Vehicle;
import fr.enssat.vehiclesrental.model.enums.VehicleType;
import fr.enssat.vehiclesrental.service.VehicleService;
import fr.enssat.vehiclesrental.service.exception.notfound.VehicleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static fr.enssat.vehiclesrental.constants.ControllerConstants.Controller.*;
import static fr.enssat.vehiclesrental.constants.ControllerConstants.VehicleController.*;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping(BASE_URL)
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Afficher la liste des véhicules
     * @param springModel Modèle
     * @param vehicleType Type du véhicule
     * @param model Modèle du véhicule
     * @param brand Marque du véhicule
     * @param nbSeats Nombre de sièges dans le véhicule
     * @return la liste des véhicules correspondant aux paramètres de requête
     */
    @GetMapping
    public String showVehicles(Model springModel,
                              @RequestParam Optional<String> vehicleType,
                              @RequestParam(defaultValue = "") String model,
                              @RequestParam(defaultValue = "") String brand,
                              @RequestParam(defaultValue = "0") int nbSeats) {
        log.info(String.format("GET %s", BASE_URL));
        springModel.addAttribute(TITLE, GetVehicles.TITLE);

        List<? extends Vehicle> vehicles;
        if (vehicleType.isPresent()) {
            VehicleType type = VehicleType.valueOf(vehicleType.get());
            switch (type) {
                case CAR:
                    vehicles = vehicleService.searchCars(brand, model, nbSeats);
                    break;
                case MOTORBIKE:
                    vehicles = vehicleService.searchMotorbikes(brand, model, nbSeats);
                    break;
                case PLANE:
                    vehicles = vehicleService.searchPlanes(brand, model, nbSeats);
                    break;
                default:
                    log.error(String.valueOf(new IllegalStateException("Unexpected value: " + vehicleType)));
                    vehicles = Collections.emptyList();
                    break;
            }
        } else {
            vehicles = vehicleService.getVehicles();
        }

        springModel.addAttribute(VEHICLES, vehicles);

        return GetVehicles.VIEW;
    }

    /**
     * Afficher les informations d'un véhicule
     * @param springModel Modèle
     * @param registration Immatriculation du véhicule
     * @return la fiche d'un véhicule
     */
    @GetMapping(GetVehicleByRegistration.URL)
    public String showVehicleByRegistration(Model springModel, @PathVariable String registration) {
        log.info(String.format("GET %s", StringUtils.replace(GetVehicleByRegistration.URL, PATTERN_REGISTRATION, registration)));
        springModel.addAttribute(TITLE, GetVehicleByRegistration.TITLE);

        Vehicle vehicle = vehicleService.getVehicleByRegistration(registration);
        springModel.addAttribute(VEHICLE, vehicle);

        return GetVehicleByRegistration.VIEW;
    }

    /**
     * Afficher le formulaire d'ajout d'un véhicule
     * @param springModel Modèle
     * @param vehicleType Type du véhicule
     * @return le formulaire d'ajout
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @GetMapping(AddVehicle.URL)
    public String showAddVehicle(Model springModel, @RequestParam Optional<String> vehicleType) {
        log.info(String.format("GET %s", AddVehicle.URL));
        springModel.addAttribute(TITLE, AddVehicle.TITLE);

        Vehicle vehicle = null;
        if (vehicleType.isPresent()) {
            VehicleType type = VehicleType.valueOf(vehicleType.get());
            switch (type) {
                case CAR:
                    vehicle = new Car();
                    break;
                case MOTORBIKE:
                    vehicle = new Motorbike();
                    break;
                case PLANE:
                    vehicle = new Plane();
                    break;
                default:
                    log.error(String.valueOf(new IllegalStateException("Unexpected value: " + vehicleType)));
                    break;
            }
        }

        springModel.addAttribute(VEHICLE, vehicle);

        return AddVehicle.VIEW;
    }

    /**
     * Ajouter une voiture
     * @param vehicle Vehicule
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @param <T> Type du véhicule
     * @return la fiche du véhicule enregistrée ou le formulaire d'ajout avec les erreurs
     */
    private <T extends Vehicle> String addVehicle(T vehicle,
                                                  BindingResult result,
                                                  Model springModel,
                                                  RedirectAttributes redirectAttributes) {
        log.info(String.format("Add vehicle %s", vehicle.getRegistration()));
        springModel.addAttribute(TITLE, AddVehicle.TITLE);

        // Check if form has errors
        if (result.hasErrors()) {
            log.info(result.toString());

            // Return form with errors
            return AddVehicle.VIEW;
        }

        try {
            Vehicle existedVehicle = vehicleService.getVehicleByRegistration(vehicle.getRegistration());
            if (existedVehicle != null) {
                result.rejectValue("registration", "vehicle.registration",
                        "Le numéro d'immatriculation est déjà attribué pour un autre véhicule");

                // Return form with errors
                return AddVehicle.VIEW;
            }
        } catch (VehicleNotFoundException vehicleNotFoundException) {
            try {
                // Save vehicle
                vehicle = (T) vehicleService.addVehicle(vehicle);
            } catch (Exception exception) {
                log.error(exception.getMessage() + exception.getCause());
                redirectAttributes.addFlashAttribute(MESSAGE, AddVehicle.ERROR_MESSAGE);

                return REDIRECT_VEHICLES;
            }
        }

        return String.format(REDIRECT_VEHICLE_BY_REGISTRATION, vehicle.getRegistration());
    }

    /**
     * Ajouter une voiture
     * @param car Instance d'une voiture
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'une voiture ou le formulaire d'ajout avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(AddVehicle.URL_CAR)
    public String addCar(@Valid @ModelAttribute(VEHICLE) Car car, BindingResult result, Model springModel, RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", AddVehicle.URL_CAR));
        return addVehicle(car, result, springModel, redirectAttributes);
    }

    /**
     * Ajouter une moto
     * @param motorbike Instance d'une moto
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'une moto ou le formulaire d'ajout avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(AddVehicle.URL_MOTORBIKE)
    public String addMotorbike(@Valid @ModelAttribute(VEHICLE) Motorbike motorbike, BindingResult result, Model springModel, RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", AddVehicle.URL_MOTORBIKE));
        return addVehicle(motorbike, result, springModel, redirectAttributes);
    }

    /**
     * Ajouter un avion
     * @param plane Instance d'un avion
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'un avion ou le formulaire d'ajout avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(AddVehicle.URL_PLANE)
    public String addPlane(@Valid @ModelAttribute(VEHICLE) Plane plane, BindingResult result, Model springModel, RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", AddVehicle.URL_PLANE));
        return addVehicle(plane, result, springModel, redirectAttributes);
    }

    /**
     * Afficher le formulaire de modification d'un véhicule
     * @param springModel Modèle
     * @param registration Immatriculation du véhicule
     * @return le formulaire de modification d'un véhicule
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @GetMapping(UpdateVehicle.URL)
    public String showUpdateVehicle(Model springModel, @PathVariable String registration) {
        log.info(String.format("GET %s", StringUtils.replace(UpdateVehicle.URL, PATTERN_REGISTRATION, registration)));
        springModel.addAttribute(TITLE, UpdateVehicle.TITLE);

        Vehicle vehicle = vehicleService.getVehicleByRegistration(registration);
        springModel.addAttribute(VEHICLE, vehicle);

        return UpdateVehicle.VIEW;
    }

    /**
     * Met à jour un véhicule
     * @param vehicle Véhicule
     * @param registration Immatriculation du véhicule
     * @param result Binding result
     * @param redirectAttributes Redirect attributes
     * @return la fiche du véhicule mise à jour ou le formulaire de modification avec les erreurs
     */
    private String updateVehicle(@Valid @ModelAttribute(VEHICLE) Vehicle vehicle,
                                String registration,
                                BindingResult result,
                                Model springModel,
                                RedirectAttributes redirectAttributes) {
        log.info(String.format("Update vehicle %s", registration));
        springModel.addAttribute(TITLE, UpdateVehicle.TITLE);

        if (result.hasErrors()) {
            log.info(result.toString());

            // Return form with errors
            return UpdateVehicle.VIEW;
        }

        try {
            // Update vehicle
            if (vehicleService.exists(vehicle.getId())) {
                vehicle = vehicleService.editVehicle(vehicle);
            } else {
                throw new Exception("Le véhicule n'existe pas " + vehicle.getId());
            }
        } catch (Exception exception) {
            log.error(exception.getMessage() + exception.getCause());
            redirectAttributes.addFlashAttribute(MESSAGE, UpdateVehicle.ERROR_MESSAGE);

            return REDIRECT_VEHICLES;
        }

        return String.format(REDIRECT_VEHICLE_BY_REGISTRATION, vehicle.getRegistration());
    }

    /**
     * Met à jour la fiche d'une voiture
     * @param car Instance d'une voiture
     * @param registration Immatriculation de la voiture
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'une voiture ou le formulaire de modification avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(UpdateVehicle.URL_CAR)
    public String updateCar(@Valid @ModelAttribute(VEHICLE) Car car,
                            @PathVariable String registration,
                            BindingResult result,
                            Model springModel,
                            RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", StringUtils.replace(UpdateVehicle.URL_CAR, PATTERN_REGISTRATION, registration)));
        return updateVehicle(car, registration, result, springModel, redirectAttributes);
    }

    /**
     * Met à jour la fiche d'une moto
     * @param motorbike Instance d'une moto
     * @param registration Immatriculation de la moto
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'une moto ou le formulaire de modification avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(UpdateVehicle.URL_MOTORBIKE)
    public String updateMotorbike(@Valid @ModelAttribute(VEHICLE) Motorbike motorbike,
                                  @PathVariable String registration,
                                  BindingResult result,
                                  Model springModel,
                                  RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", StringUtils.replace(UpdateVehicle.URL_MOTORBIKE, PATTERN_REGISTRATION, registration)));
        return updateVehicle(motorbike, registration, result, springModel, redirectAttributes);
    }

    /**
     * Met à jour la fiche d'un avion
     * @param plane Instance d'un avion
     * @param registration Immatriculation de l'avion
     * @param result Binding result
     * @param springModel Modèle
     * @param redirectAttributes Redirect attributes
     * @return la fiche d'un avion ou le formulaire de modification avec les erreurs
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @PostMapping(UpdateVehicle.URL_PLANE)
    public String updatePlane(@Valid @ModelAttribute(VEHICLE) Plane plane,
                              @PathVariable String registration,
                              BindingResult result,
                              Model springModel,
                              RedirectAttributes redirectAttributes) {
        log.info(String.format("POST %s", StringUtils.replace(UpdateVehicle.URL_PLANE, PATTERN_REGISTRATION, registration)));
        return updateVehicle(plane, registration, result, springModel, redirectAttributes);
    }

    /**
     * Archiver un véhicule
     * @param id ID du véhicule
     * @param redirectAttributes Redirect attributes
     * @return la liste des véhicules ou un message d'erreur si l'archivage échoue
     */
    @PreAuthorize(value = "hasAnyAuthority(T(fr.enssat.vehiclesrental.model.enums.Position).RESPONSABLE_LOCATION.label, T(fr.enssat.vehiclesrental.model.enums.Position).GESTIONNAIRE_TECHNIQUE.label)")
    @GetMapping(ArchiveVehicle.URL)
    public String archiveVehicle(@PathVariable String id,
                              RedirectAttributes redirectAttributes) {
        log.info(String.format("GET %s", StringUtils.replace(ArchiveVehicle.URL, PATTERN_ID, id)));

        try {
            // Archive vehicle
            vehicleService.archiveVehicle(Long.parseLong(id));
        } catch (Exception exception) {
            log.error(exception.getMessage() + exception.getCause());
            redirectAttributes.addFlashAttribute(MESSAGE, ArchiveVehicle.ERROR_MESSAGE);
        }

        return REDIRECT_VEHICLES;
    }
}
