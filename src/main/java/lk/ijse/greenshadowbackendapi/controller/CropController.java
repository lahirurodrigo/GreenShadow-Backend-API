package lk.ijse.greenshadowbackendapi.controller;

import lk.ijse.greenshadowbackendapi.dto.CropStatus;
import lk.ijse.greenshadowbackendapi.dto.impl.CropDTO;
import lk.ijse.greenshadowbackendapi.dto.impl.FieldDTO;
import lk.ijse.greenshadowbackendapi.exception.CropNotFoundException;
import lk.ijse.greenshadowbackendapi.exception.DataPersistException;
import lk.ijse.greenshadowbackendapi.service.CropService;
import lk.ijse.greenshadowbackendapi.util.AppUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/crop")
@CrossOrigin
public class CropController {

    @Autowired
    private CropService cropService;
    // save crop details
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CropDTO> saveCrop(
            @RequestParam String cropCode,
            @RequestParam String cropCommonName,
            @RequestParam String cropScientificName,
            @RequestParam("cropImage") MultipartFile cropImage,  // for file upload
            @RequestParam String cropCategory,
            @RequestParam String cropSeason
    ) {
        String base64CropImage = "";
        try {
            byte[] bytesCropImage = cropImage.getBytes();
            base64CropImage = AppUtil.cropImageToBase64(bytesCropImage);

            // Set the values for the CropDTO
            CropDTO buildCropDTO = new CropDTO();
            buildCropDTO.setCropCode(cropCode);
            buildCropDTO.setCropCommonName(cropCommonName);
            buildCropDTO.setCropScientificName(cropScientificName);
            buildCropDTO.setCropImage(base64CropImage);  // Set the base64 image data
            buildCropDTO.setCropCategory(cropCategory);
            buildCropDTO.setCropSeason(cropSeason);
            cropService.saveCrop(buildCropDTO);

        }catch (DataPersistException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            return null;

    }

    // Get all crops
    @GetMapping
    public List<CropDTO> getAllCrops() {
        return cropService.getAllCrops();
    }

    // Get a crop by id
    @GetMapping("/{code}")
    public CropStatus getCropById(@PathVariable String code) {
        return cropService.getCrop(code);
    }

    // Update a crop
    @PutMapping("/{code}")
    public ResponseEntity<Void> updateCrop(
            @PathVariable String code,
            @RequestParam String cropCommonName,
            @RequestParam String cropScientificName,
            @RequestParam("cropImage") MultipartFile cropImage,  // for file upload
            @RequestParam String cropCategory,
            @RequestParam String cropSeason
    ) {
        String base64CropImage = "";

        try {

            byte[] bytesCropImage = cropImage.getBytes();
            base64CropImage = AppUtil.cropImageToBase64(bytesCropImage);

            // Set the values for the CropDTO
            CropDTO buildCropDTO = new CropDTO();
            buildCropDTO.setCropCode(code);
            buildCropDTO.setCropCommonName(cropCommonName);
            buildCropDTO.setCropScientificName(cropScientificName);
            buildCropDTO.setCropImage(base64CropImage);  // Set the base64 image data
            buildCropDTO.setCropCategory(cropCategory);
            buildCropDTO.setCropSeason(cropSeason);
            cropService.updateCrop(code, buildCropDTO);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CropNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // Delete a crop
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteField(@PathVariable String id) {
        try{
            cropService.deleteCrop(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (CropNotFoundException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}