package test

import (
	"testing"

	"github.com/gruntwork-io/terratest/modules/terraform"
	"github.com/stretchr/testify/assert"
)

// TestTerraformBasicExample runs a basic test on the Terraform code
func TestTerraformBasicExample(t *testing.T) {
	// Construct the terraform options with default retryable errors
	terraformOptions := terraform.WithDefaultRetryableErrors(t, &terraform.Options{
		// Set the path to the Terraform code that will be tested
		TerraformDir: "../../",

		// Variables to pass to our Terraform code using -var-file options
		VarFiles: []string{"tests/plan/test.tfvars"},

 	// Disable colors in Terraform commands so its easier to parse stdout/stderr
 	NoColor: true,

 	// Don't actually build anything
		PlanOnly:  true,
		Lock:      false,
		NoBackend: true,
	})

	// Run `terraform init` and `terraform plan`. Fail the test if there are any errors.
	planOutput := terraform.InitAndPlan(t, terraformOptions)

	// Verify the plan output contains the expected resources
	assert.Contains(t, planOutput, "aws_vpc.main")
	assert.Contains(t, planOutput, "aws_eks_cluster.main")
	assert.Contains(t, planOutput, "aws_s3_bucket.resumes")
	assert.Contains(t, planOutput, "aws_s3_bucket.candidates_application")
	assert.Contains(t, planOutput, "aws_s3_bucket.recruitment_application")
	assert.Contains(t, planOutput, "aws_db_instance.main")
	assert.Contains(t, planOutput, "aws_cloudfront_distribution.main")
}
