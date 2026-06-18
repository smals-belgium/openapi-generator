package org.openapitools.codegen.smals;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;



import java.util.Objects;

/**
 * SomeEnumObject
 */

public class SomeEnumObject {

    /**
     * Gets or Sets myFirstEnum
     */
    public enum MyFirstEnumEnum {
        FIRST("first"),

        SECOND("second"),

        THIRD("third");

        private final String value;

        MyFirstEnumEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static MyFirstEnumEnum fromValue(String value) {
            for (MyFirstEnumEnum b : MyFirstEnumEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private MyFirstEnumEnum myFirstEnum;

    public SomeEnumObject myFirstEnum(MyFirstEnumEnum myFirstEnum) {
        this.myFirstEnum = myFirstEnum;
        return this;
    }

    /**
     * Get myFirstEnum
     * @return myFirstEnum
     */

    @JsonProperty("myFirstEnum")
    public MyFirstEnumEnum getMyFirstEnum() {
        return myFirstEnum;
    }

    public void setMyFirstEnum(MyFirstEnumEnum myFirstEnum) {
        this.myFirstEnum = myFirstEnum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SomeEnumObject someEnumObject = (SomeEnumObject) o;
        return Objects.equals(this.myFirstEnum, someEnumObject.myFirstEnum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myFirstEnum);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SomeEnumObject {\n");
        sb.append("    myFirstEnum: ").append(toIndentedString(myFirstEnum)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

    public static class Builder {

        private SomeEnumObject instance;

        public Builder() {
            this(new SomeEnumObject());
        }

        protected Builder(SomeEnumObject instance) {
            this.instance = instance;
        }

        protected Builder copyOf(SomeEnumObject value) {
            this.instance.setMyFirstEnum(value.myFirstEnum);
            return this;
        }

        public SomeEnumObject.Builder myFirstEnum(MyFirstEnumEnum myFirstEnum) {
            this.instance.myFirstEnum(myFirstEnum);
            return this;
        }

        /**
         * returns a built SomeEnumObject instance.
         *
         * The builder is not reusable (NullPointerException)
         */
        public SomeEnumObject build() {
            try {
                return this.instance;
            } finally {
                // ensure that this.instance is not reused
                this.instance = null;
            }
        }

        @Override
        public String toString() {
            return getClass() + "=(" + instance + ")";
        }
    }

    /**
     * Create a builder with no initialized field (except for the default values).
     */
    public static SomeEnumObject.Builder builder() {
        return new SomeEnumObject.Builder();
    }

    /**
     * Create a builder with a shallow copy of this instance.
     */
    public SomeEnumObject.Builder toBuilder() {
        SomeEnumObject.Builder builder = new SomeEnumObject.Builder();
        return builder.copyOf(this);
    }

}

