import { TextField, TextFieldProps } from "@mui/material";
import { useField } from "formik";

interface Props {
  name: string;
}

const FormTextInput: React.FunctionComponent<Props & TextFieldProps> = ({
  name,
  ...props
}) => {
  const [field, meta] = useField(name);
  return (
    <TextField
      type="text"
      variant="standard"
      id={name}
      {...props}
      name={name}
      value={field.value}
      onChange={field.onChange}
      onBlur={field.onBlur}
      error={meta.touched && Boolean(meta.error)}
      helperText={meta.touched && meta.error}
    />
  );
};

export default FormTextInput;
