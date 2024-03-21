package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.types.TypeName;
import java.util.List;
import java.util.Optional;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.SignatureAttribute.ArrayType;
import javassist.bytecode.SignatureAttribute.ClassSignature;
import javassist.bytecode.SignatureAttribute.ClassType;
import javassist.bytecode.SignatureAttribute.TypeArgument;
import lombok.experimental.UtilityClass;

import static org.apache.commons.lang3.StringUtils.substringAfter;

@UtilityClass
public class SignatureUtils {

    public static ClassSignature createClassSignature(TypeName superType, List<TypeName> interfaces) {
        final ClassType superObjectType = Optional.ofNullable(superType)
                .map(SignatureUtils::toObjectType)
                .map(ClassType.class::cast)
                .orElse(null);

        final ClassType[] interfaceTypes = Optional.ofNullable(interfaces).stream()
                .flatMap(List::stream)
                .map(SignatureUtils::toObjectType)
                .map(ClassType.class::cast)
                .toArray(ClassType[]::new);

        return new ClassSignature(null, superObjectType, interfaceTypes);
    }

    private static SignatureAttribute.ObjectType toObjectType(TypeName typeName) {

        final TypeArgument[] args = typeName.getArguments().stream()
                .map(tn -> tn.getName().startsWith("?")
                        ? getWildcardArgument(tn.getName())
                        : new TypeArgument(toObjectType(tn))
                )
                .toArray(TypeArgument[]::new);

        if (typeName.getDimension() > 0) {
            final SignatureAttribute.Type type = typeName.isPrimitive()
                    ? new SignatureAttribute.BaseType(typeName.getName())
                    : new ClassType(typeName.getName(), args.length == 0 ? null : args);
            return new ArrayType(typeName.getDimension(), type);
        } else {
            return new ClassType(typeName.getName(), args.length == 0 ? null : args);
        }
    }

    private TypeArgument getWildcardArgument(String name) {
        if (name.contains(" extends ")) {
            return TypeArgument.subclassOf(toObjectType(TypeName.parse(substringAfter(name, "extends "))));
        } else if (name.contains(" super ")) {
            return TypeArgument.superOf(toObjectType(TypeName.parse(substringAfter(name, "super "))));
        } else {
            return new TypeArgument();
        }
    }

}
