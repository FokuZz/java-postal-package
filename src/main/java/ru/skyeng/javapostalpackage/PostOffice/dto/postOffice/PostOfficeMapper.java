package ru.skyeng.javapostalpackage.PostOffice.dto.postOffice;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.skyeng.javapostalpackage.PostOffice.model.PostOffice;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostOfficeMapper {
    public static PostOffice toOffice(PostOfficeDto postOfficeDto) {
        return PostOffice.builder()
                .id(postOfficeDto.getId())
                .postalIndex(postOfficeDto.getPostalIndex())
                .name(postOfficeDto.getName())
                .address(postOfficeDto.getAddress())
                .build();
    }

    public static PostOfficeDto toOfficeDto(PostOffice postOffice) {
        return PostOfficeDto.builder()
                .id(postOffice.getId())
                .postalIndex(postOffice.getPostalIndex())
                .name(postOffice.getName())
                .address(postOffice.getAddress())
                .build();
    }

    public static List<PostOfficeDto> toOfficeDto(Iterable<PostOffice> officeDtos) {
        List<PostOfficeDto> postOfficeDtos = new ArrayList<>();
        for (PostOffice postOffice : officeDtos) {
            postOfficeDtos.add(toOfficeDto(postOffice));
        }
        return postOfficeDtos;
    }
}
