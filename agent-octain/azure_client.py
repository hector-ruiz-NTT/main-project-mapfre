import os
from dotenv import load_dotenv
from openai import AzureOpenAI

load_dotenv()

AZURE_API_KEY = os.environ["AZURE_OPENAI_API_KEY"]
AZURE_API_VERSION = os.environ["AZURE_OPENAI_API_VERSION"]
AZURE_ENDPOINT = os.environ["AZURE_OPENAI_ENDPOINT"]
DEPLOYMENT_NAME = os.environ["AZURE_OPENAI_DEPLOYMENT"]

client = AzureOpenAI(
    api_key=AZURE_API_KEY,
    api_version=AZURE_API_VERSION,
    azure_endpoint=AZURE_ENDPOINT
)