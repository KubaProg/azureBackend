"use client";
import FileUploadButton from "@/components/file-upload-button";
import FormTextInput from "@/components/form-text-input";
import { Button } from "@mui/material";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { useMutation } from "@tanstack/react-query";
import { Form, Formik } from "formik";
import * as Yup from "yup";

const API_URL = "http://ec2-54-196-11-208.compute-1.amazonaws.com:8080";

interface FormValues {
  length: string;
  keywords: string;
  file: File | null;
}

export default function HomePage() {
  const { mutate } = useMutation({
    mutationKey: ["uploadDocument"],
    mutationFn: async ({ file, keywords, length }: FormValues) => {
      const formData = new FormData();

      formData.append("file", file as File);
      formData.append("length", length.toString());
      formData.append("keywords", keywords);

      const response = await fetch(`${API_URL}/summarize`, {
        method: "POST",
        body: formData,
      });
      const data = await response.json();
      console.log(data);
      return data;
    },
    onError: (error) => {
      console.log(error);
    },
    onSuccess: (data) => {
      console.log(data);
    },
  });

  return (
    <Box sx={{ display: "flex", minHeight: "100vh", justifyContent: "center" }}>
      <Box
        sx={{
          width: "500px",
        }}
      >
        <Typography
          sx={{
            marginTop: "80px",
          }}
          variant="h4"
          fontWeight={600}
        >
          Document Anylysis Application
        </Typography>
        <Typography
          sx={{
            marginTop: "16px",
          }}
          variant="body1"
        >
          Fill in the form below to upload a document for analysis.
        </Typography>
        <Formik
          onSubmit={(vals) => mutate(vals)}
          initialValues={{
            length: "",
            keywords: "",
            file: null,
          }}
          validateOnChange
          validationSchema={Yup.object({
            length: Yup.number()
              .typeError("Must be a number")
              .required("Required")
              .min(1, "Must be greater than 0")
              .max(100, "Must be less than 100"),
            keywords: Yup.string()
              .required("Required")
              .test(
                "is-comma-separated",
                "Keywords must be comma-separated",
                (value) => {
                  if (!value) {
                    return true; // Empty value is considered valid
                  }
                  const keywordArray = value.split(",");
                  return keywordArray.every(
                    (keyword) => keyword.trim().length > 0,
                  );
                },
              ),
            file: Yup.mixed().required("Required"),
          })}
        >
          {({ errors, values }) => (
            <Form>
              <Box
                sx={{
                  display: "flex",
                  flexDirection: "column",
                  gap: "16px",
                  marginTop: "24px",
                }}
              >
                <FormTextInput
                  name="keywords"
                  label="Keywords"
                  placeholder="Comma separated keywords..."
                  variant="outlined"
                />
                <FormTextInput
                  name="length"
                  label="Length of output"
                  placeholder="Enter the number of words in the output..."
                  variant="outlined"
                />
                <FileUploadButton name="file" types={["pdf"]} />
              </Box>
              <Button
                type="submit"
                variant="contained"
                sx={{ marginTop: "16px" }}
              >
                Submit
              </Button>
            </Form>
          )}
        </Formik>
      </Box>
    </Box>
  );
}
